// user.js

function User(data, fields) {
    console.log("user.js -> User: called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserViewModel(params, config) {
    console.log("user.js -> UserViewModel:", params, config);
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.maxPage = ko.computed(() => {
        const total = self.total() || 0;
        const size = self.size() || 1;
        return Math.max(1, Math.ceil(total / size));
    });
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    self.resolveDbField = function(uiField) {
        const col = self.gridConfig?.columns?.find(c => c.name === uiField);
        return col?.dbField || uiField;
    };

    self.buildSearchQuery = function() {
        console.log("user.js -> buildSearchQuery: called");
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
        return params.toString();
    };

    // ========== Use /search endpoint ==========
    self.loadUsers = async function() {
        console.log("user.js -> loadUsers: called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const url = `${API_USER}/search?${qs}`;
            console.log("user.js -> loadUsers: url=", url);
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("user.js -> loadUsers: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.users(paged.items.map(u => new User(u, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount) self.total(paged.totalCount);
            } else {
                self.users([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load users error:', err);
            self.users([]);
            self.total(0);
        }
    };

    self.searchUsers = function() {
        console.log("user.js -> searchUsers: called");
        self.page(1);
        self.loadUsers();
    };

    self.resetSearch = function() {
        console.log("user.js -> resetSearch: called");
        Object.keys(self.searchParams).forEach(k => this.searchParams[k](''));
        self.page(1);
        self.loadUsers();
    };

    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.loadUsers();
        }
    };

    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.loadUsers();
        }
    };

    self.size.subscribe(() => {
        self.page(1);
        self.loadUsers();
    });

    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUsers();
    };

    self.navigateToUsers = function() {
        window.location.href = 'users.jsp?reload=' + new Date().getTime();
    };

    self.addUser = function() {
        window.location.href = 'addUser.jsp';
    };

    self.editUser = function(user) {
        console.log("user.js -> editUser: user=", user);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editUserId', ko.unwrap(user.id));
        window.location.href = 'editUser.jsp';
    };

    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        console.log("user.js -> invokeAction: action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

    self.validateForm = function(userObj, fieldsConfig) {
        console.log("user.js -> calling Validator.validateForm");
        return self.validator
            ? self.validator.validateForm(userObj, fieldsConfig)
            : {};
    };

    self.saveUser = async function() {
        console.log("user.js -> saveUser: called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentUser(), self.formConfig.fields);
        console.log("user.js -> saveUser: errs=", errs);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentUser()[f.name]());

        try {
            let url = `${API_USER}/create`;
            let method = 'POST';
            if (self.mode === 'edit' && self.currentUser().id && self.currentUser().id()) {
                const idVal = self.currentUser().id();
                url = `${API_USER}/update?id=${encodeURIComponent(idVal)}`;
                method = 'PUT';
            }
			console.log("user.js -> saveUser: self.mode=", self.mode);
			console.log("user.js -> saveUser: self.currentUser()=", self.currentUser());
            console.log("user.js -> saveUser: url=", url);
			console.log("user.js -> saveUser: method=", method);
			console.log("user.js -> saveUser: payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToUsers();
        } catch (err) {
            console.error('Save user error:', err);
        }
    };

    self.deleteUser = async function(user) {
        console.log("user.js -> deleteUser: user=", user);
        if (!confirm('Are you sure?')) return;
        const idVal = ko.unwrap(user.id);
        try {
            const url = `${API_USER}/delete?id=${encodeURIComponent(idVal)}`;
            console.log("user.js -> deleteUser: url=", url);
            await fetch(url, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUsers();
        } catch (err) {
            console.error('Delete user error:', err);
        }
    };
	
	self.resetPassword = async function(user) {
		console.log("user.js -> resetPassword: user=", user);
		if (!confirm('Are you sure?')) return;
		localStorage.setItem('editUserId', ko.unwrap(user.id));
		window.location.href = 'resetPassword.jsp';
	};

    self.loadUserById = async function(id) {
        console.log("user.js -> loadUserById: id=", id);
        try {
            const url = `${API_USER}/get?id=${encodeURIComponent(id)}`;
            console.log("user.js -> loadUserById: url=", url);
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("user.js -> loadUserById: data=", data);
            if (data.status === 'SUCCESS' && data.data) {
                self.currentUser(new User(data.data, self.formConfig?.fields || []));
            }
        } catch (err) {
            console.error('Load user error:', err);
        }
    };

    console.log("user.js -> Initialization block: called");
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editUserId');
        console.log("user.js -> edit id=", id);
        if (id) self.loadUserById(id);
    } else if (self.mode === 'add') {
        console.log("user.js -> add mode");
        self.currentUser(new User({}, self.formConfig?.fields || []));
    } else {
        console.log("user.js -> list mode");
        self.loadUsers();
    }

    console.log("user.js -> Wrapper block: called");
    self.currentObject = self.currentUser;
    self.objects = self.users;
}

export { User, UserViewModel };
