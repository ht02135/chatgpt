// userList.js

import FileUploader from "../../js/fileUploader.js";   // ✅ now works as ES module

// const API_USERLIST = '/chatgpt/api/management/userlists';
// dynamically figure out the context path from window.location.pathname
const USERTLIST_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_USERLIST = `${USERTLIST_CONTEXT_PATH}/api/management/userlists`;

function UserList(data, fields) {
    console.log("userList.js -> UserList: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserListViewModel(params, config) {
	console.log("userList.js -> UserListViewModel: constructor called");
	console.log("userList.js -> UserListViewModel=", params, config);
    const self = this;

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
        console.log("userList.js -> params=", params);
        return params.toString();
    };

    // ========================
    // Load UserLists
    // ========================
    self.loadUserLists = async function() {
        console.log("userList.js -> loadUserLists called");
        console.log("userList.js -> loadUserLists: self.mode=", self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
			console.log("userList.js -> loadUserLists: qs=", qs);
            const res = await fetch(`${API_USERLIST}?${qs}`, { headers: { 'Accept': 'application/json' } });
			console.log("userList.js -> loadUserLists: #############");
			console.log("userList.js -> loadUserLists: res=", res);
			console.log("userList.js -> loadUserLists: #############");
			const data = await res.json();
			console.log("userList.js -> loadUserLists: data=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
				console.log("userList.js -> loadUserLists: #############");
				console.log("userList.js -> loadUserLists: paged=", paged);
				console.log("userList.js -> loadUserLists: #############");
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
    self.navigateToUserLists = function() { window.location.href = 'userLists.jsp'; };
    self.addUserList = function() { window.location.href = 'addUserList.jsp'; };

	self.editUserList = function(userList) {
		console.log("userList.js -> editUserList: #############");
		console.log("userList.js -> editUserList: userList=", userList);
		console.log("userList.js -> editUserList: ko.unwrap(userList.id)=", ko.unwrap(userList.id));
		console.log("userList.js -> editUserList: #############");
		
		if (!confirm('Are you sure?')) return;
	    localStorage.setItem('editUserListId', ko.unwrap(userList.id));
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
			console.log("userList.js -> invokeAction: action=", action);
			console.log("userList.js -> invokeAction: action.jsMethod=", action.jsMethod);
			self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // Validation & Save
    self.validateForm = function(obj, fields) { return self.validator ? self.validator.validateForm(obj, fields) : {}; };

	// hung: for now, saveUserList just navigate to userLists.jsp
    self.saveUserList = async function() {
        console.log("userList.js -> saveUserList called");
		window.location.href = 'userLists.jsp';
    };
	
	// hung: VM upload method using new FileUploader
	self.uploadUserList = async function() {
	    console.log("userList.js -> uploadUserList called #############");

	    try {
	        // 1️⃣ Create uploader and pass VM callback to provide payload
	        const uploader = new FileUploader(`${API_USERLIST}/import`, (file) => {
	            console.log("userList.js -> payloadProvider called for file=", file.name);

	            // Build payload from currentUserList or fallback from file
	            let payloadObj = ko.toJS(self.currentUserList()) || {};
	            if (!payloadObj.userListName) {  // <-- use camelCase matching POJO
	                const nameWithoutExt = file.name.replace(/\.[^/.]+$/, "");
	                payloadObj.userListName = nameWithoutExt;
	                payloadObj.originalFileName = file.name;
	                payloadObj.description = nameWithoutExt;
	            }

	            console.log("userList.js -> payloadProvider returning payloadObj=", payloadObj);
	            return payloadObj;
	        });

	        // 2️⃣ Call upload (uploader will prompt file and use payloadProvider)
	        const result = await uploader.upload();

	        // 3️⃣ Handle response
	        if (result.success) {
	            console.log("userList.js -> uploadUserList: import success", result);
	            self.navigateToUserLists();
	        } else {
	            console.error("userList.js -> uploadUserList: validation or server error", result.errors);
	            self.errors(result.errors || {});
	        }
	    } catch (err) {
	        console.error("userList.js -> uploadUserList: unexpected error", err);
	        self.errors({ network: err.message });
	    }
	};
	
	/*
	Hung : dont delete this
	self.downloadSampleUserList = async function() {
	    console.log("userList.js -> downloadSampleUserList called #############");
	    window.location.href = 'data/management/user_lists/test_user_lists_1.csv';
	};
	*/
	self.downloadSampleUserList = async function() {
	    console.log("userList.js -> downloadSampleUserList called #############");
	    // Hit the API endpoint that streams the file
	    window.location.href = `${API_USERLIST}/download/sample`;
	};
	
	self.exportCSVUserList = async function(userList) {
		console.log("userList.js -> exportCSVUserList: #############");
		console.log("userList.js -> exportCSVUserList: userList=", userList);
		console.log("userList.js -> exportCSVUserList: ko.unwrap(userList.id)=", ko.unwrap(userList.id));
		console.log("userList.js -> exportCSVUserList: #############");

		if (!confirm('Are you sure you want to export CSV?')) return;
		const id = ko.unwrap(userList.id);
		console.log("Export CSV userListId=", id);
		window.location.href = `${API_USERLIST}/export/csv?listId=${encodeURIComponent(id)}`;
	};
	
	self.exportExcelUserList = async function(userList) {
		console.log("userList.js -> exportCSVUserList: #############");
		console.log("userList.js -> exportCSVUserList: userList=", userList);
		console.log("userList.js -> exportCSVUserList: ko.unwrap(userList.id)=", ko.unwrap(userList.id));
		console.log("userList.js -> exportCSVUserList: #############");

		if (!confirm('Are you sure you want to export Excel?')) return;
		const id = ko.unwrap(userList.id);
		console.log("Export Excel userListId=", id);
		window.location.href = `${API_USERLIST}/export/excel?listId=${encodeURIComponent(id)}`;
	};

    // Delete
    self.deleteUserList = async function(userList) {
		console.log("userList.js -> deleteUserList: #############");
		console.log("userList.js -> deleteUserList: userList=", userList);
		console.log("userList.js -> deleteUserList: ko.unwrap(userList.id)=", ko.unwrap(userList.id));
		console.log("userList.js -> deleteUserList: #############");
		
        if (!confirm('Are you sure?')) return;
        try {
            await fetch(`${API_USERLIST}/delete?listId=${encodeURIComponent(ko.unwrap(userList.id))}`, {
                method: 'DELETE',
                headers: { 'Accept': 'application/json' }
            });
            self.loadUserLists();
        } catch (err) { console.error('Delete userList error:', err); }
    };

    // Load by ID
    self.loadUserListById = async function(listId) {
		console.log("userList.js -> loadUserListById called");
		console.log("userList.js -> loadUserListById listId=",listId);
        try {
            const res = await fetch(`${API_USERLIST}/get?listId=${encodeURIComponent(listId)}`, { headers: { 'Accept': 'application/json' } });
			console.log("userList.js -> loadUserListById res=",res);
			const data = await res.json();
			console.log("userList.js -> loadUserListById data=",data);
			
            if (data.status === 'SUCCESS' && data.data) self.currentUserList(new UserList(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load userList error:', err); }
    };

    // Initialization
    if (self.mode === 'edit') {
        const listId = localStorage.getItem('editUserListId');
        if (listId) self.loadUserListById(listId);
    } else if (self.mode === 'add') {
        self.currentUserList(new UserList({}, self.formConfig?.fields || []));
    } else {
        self.loadUserLists();
    }

    // Wrapper
    self.currentObject = self.currentUserList;
    self.objects = self.userLists;
}

// ✅ export so JSP can import
export { UserList, UserListViewModel };
