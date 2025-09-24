// userList.js

const API_USERLIST = '/chatgpt/api/management/userlists';

function UserList(data, fields) {
    console.log("userList.js -> UserList: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserListViewModel(params, config) {
    const self = this;

    console.log("userList.js -> UserListViewModel: constructor called");

    // ========================
    // Mode & Configs
    // ========================
	/*
	Hung : mode is treated as object
	self.mode = mode || 'list';
	*/
	/*
	Hung: params is the object you pass ({ mode: "list" }).
	      params.mode correctly accesses the string "list".
	*/
	self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    // ========================
    // Observables
    // ========================
    self.userLists = ko.observableArray([]);
    self.currentUserList = ko.observable(new UserList({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    // Pagination
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.maxPage = ko.computed(() => Math.max(1, Math.ceil((self.total() || 0) / (self.size() || 1))));
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    // Helper: resolve UI field -> DB field
    self.resolveDbField = function(uiField) {
        const col = self.gridConfig?.columns?.find(c => c.name === uiField);
        return col?.dbField || uiField;
    };

    self.buildSearchQuery = function() {
		console.log("userList.js -> buildSearchQuery called");
        const params = new URLSearchParams();
        params.append('page', self.page() - 1);
        params.append('size', self.size());
        params.append('sortField', self.resolveDbField(self.sortField()));
        params.append('sortDirection', self.sortOrder());

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = self.searchParams[f.name]();
                if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
            });
        }
		console.log("userList.js -> params=",params);
        return params.toString();
    };

    // ========================
    // Load UserLists
    // ========================
    self.loadUserLists = async function() {
        console.log("userList.js -> loadUserLists called");
		console.log("userList.js -> self.mode=",self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
			console.log("userList.js -> qs=", qs);
            const res = await fetch(`${API_USERLIST}?${qs}`, { headers: { 'Accept': 'application/json' } });
			console.log("userList.js -> #############=");
			console.log("userList.js -> res=", res);
			console.log("userList.js -> #############=");
			const data = await res.json();
			console.log("userList.js -> data=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
				console.log("userList.js -> #############=");
				console.log("userList.js -> paged=", paged);
				console.log("userList.js -> #############=");
                self.userLists(paged.items.map(u => new UserList(u, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.userLists([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load userLists error:', err);
            self.userLists([]);
            self.total(0);
        }
    };

    // ========================
    // Search & Reset
    // ========================
    self.searchUserLists = function() {
        console.log("userList.js -> searchUserLists called");
        self.page(1);
        self.loadUserLists();
    };

    self.resetSearch = function() {
        console.log("userList.js -> resetSearch called");
        self.searchParams && Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadUserLists();
    };

    // Pagination
    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.loadUserLists();
        }
    };
    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.loadUserLists();
        }
    };
    self.size.subscribe(() => {
        self.page(1);
        self.loadUserLists();
    });

    // Sorting
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUserLists();
    };

    // Navigation
    self.navigateToUserLists = function() {
        window.location.href = 'userLists.jsp';
    };
    self.addUserList = function() {
        window.location.href = 'addUserList.jsp';
    };
    self.editUserList = function(id) {
        localStorage.setItem('editUserListId', ko.unwrap(id));
        window.location.href = 'editUserList.jsp';
    };

    // Action Resolver
    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const group = self.actionGroupMap[column.actions];
        return Array.isArray(group) ? group.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            if (/^edit(UserList|Object)$/.test(action.jsMethod)) self[action.jsMethod](ko.unwrap(row.id));
            else self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // Validation & Save
    self.validateForm = function(obj, fields) {
        return self.validator ? self.validator.validateForm(obj, fields) : {};
    };

    self.saveUserList = async function() {
        console.log("userList.js -> saveUserList called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentUserList(), self.formConfig.fields);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        try {
            const uploadedFile = await FileUploader.upload("#fileInput");
            const payload = { ...ko.toJS(self.currentUserList()), file: uploadedFile };

            let url = `${API_USERLIST}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentUserList().id && self.currentUserList().id()) {
                url = `${API_USERLIST}/update?listId=${encodeURIComponent(self.currentUserList().id())}`;
                method = 'PUT';
            }
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToUserLists();
        } catch (err) { console.error('Save userList error:', err); }
    };

    // Delete
    self.deleteUserList = async function(row) {
        if (!confirm('Are you sure?')) return;
        try {
			await fetch(`${API_USERLIST}/delete?listId=${encodeURIComponent(ko.unwrap(row.id))}`, {
			    method: 'DELETE',
			    headers: { 'Accept': 'application/json' }
			});

			self.loadUserLists();
        } catch (err) { console.error('Delete userList error:', err); }
    };

    // Load by ID
    self.loadUserListById = async function(id) {
        try {
            const res = await fetch(`${API_USERLIST}/get?listId=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) self.currentUserList(new UserList(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load userList error:', err); }
    };

    // Initialization
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editUserListId');
        if (id) self.loadUserListById(id);
    } else if (self.mode === 'add') {
        self.currentUserList(new UserList({}, self.formConfig?.fields || []));
    } else {
        self.loadUserLists();
    }

    // Wrapper
    self.currentObject = self.currentUserList;
    self.objects = self.userLists;

    self.navigateToObjects = function() { return self.navigateToUserLists(); };
    self.saveObject = function() { return self.saveUserList(); };
    self.addObject = function() { return self.addUserList(); };
    self.searchObjects = function() { return self.searchUserLists(); };
}
