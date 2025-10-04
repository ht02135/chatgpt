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
    if (self.searchConfig && self.searchConfig.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');
    self.maxPage = ko.computed(() => Math.ceil(self.total() / self.size()));

    // const API_USER = '/chatgpt/api/mybatis/users';
	// Get context path dynamically from the URL
	const USER_CONTEXT_PATH = window.location.pathname.split('/')[1];
	// Construct the API endpoints dynamically
	const API_USER = `/${USER_CONTEXT_PATH}/api/mybatis/users`;
	
    // Build URLSearchParams for search
    self.buildSearchQuery = function() {
        console.log("user.js -> buildSearchQuery: called");
        const params = new URLSearchParams();
        params.append('page', self.page());
        params.append('size', self.size());
        params.append('sortField', self.sortField());
        params.append('sortOrder', self.sortOrder());

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = self.searchParams[f.name]();
                if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
            });
        }

        return params.toString();
    };

    // Load Users for Grid
    self.loadUsers = async function() {
        console.log("user.js -> loadUsers: called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_USER}/paged?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("user.js -> loadUsers: response=", data);

            if (data.status === 'SUCCESS') {
                self.users(data.data.users.map(u => new User(u, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                self.total(data.data.total || 0);
            } else {
                self.users([]);
            }
        } catch (err) {
            console.error('Load users error:', err);
            self.users([]);
        }
    };

    // Search & Reset
    self.searchUsers = function() {
        console.log("user.js -> searchUsers: called");
        self.page(1);
        self.loadUsers();
    };

    self.resetSearch = function() {
        console.log("user.js -> resetSearch: called");
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadUsers();
    };

    // Pagination
    self.nextPage = function() {
        console.log("user.js -> nextPage: called");
        if (self.page() < self.maxPage()) { 
            self.page(self.page() + 1); 
            self.loadUsers(); 
        }
    };

    self.prevPage = function() {
        console.log("user.js -> prevPage: called");
        if (self.page() > 1) { 
            self.page(self.page() - 1); 
            self.loadUsers(); 
        }
    };

    self.size.subscribe(() => {
        console.log("size.subscribe: called");
        self.page(1);
        self.loadUsers();
    });

    // Sorting
    self.setSort = function(field) {
        console.log("user.js -> setSort: field=", field);
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUsers();
    };

    // Navigation
    self.goUsers = function() {
        console.log("user.js -> goUsers: called");
        window.location.href = 'users.jsp?reload=' + new Date().getTime();
    };

    self.goAddUser = function() {
        console.log("user.js -> goAddUser: called");
        window.location.href = 'addUser.jsp';
    };

    self.goEditUser = function(id) {
        console.log("user.js -> goEditUser: id=", ko.unwrap(id));
        localStorage.setItem('editUserId', ko.unwrap(id));
        window.location.href = 'editUser.jsp';
    };

    // Actions Resolver
    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        console.log("user.js -> invokeAction called");
		console.log("user.js -> invokeAction: action=", action);
		console.log("user.js -> invokeAction: row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            if (action.jsMethod === "goEditUser") {
                console.log("user.js -> invokeAction: ko.unwrap(row.id)=", ko.unwrap(row.id));
                self[action.jsMethod](ko.unwrap(row.id));
            } else {
                self[action.jsMethod](row);
            }
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

	// ========================
	// Validation Helpers
	// ========================
	self.validateForm = function (userObj, fieldsConfig) {
	    console.log("user.js -> calling Validator.validateForm");
	    return self.validator
	        ? self.validator.validateForm(userObj, fieldsConfig)
	        : {};
	};

    // Save User
    self.saveUser = async function() {
        console.log("user.js -> saveUser: called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentUser(), self.formConfig.fields);
        console.log("user.js ##########");
        console.log("user.js -> saveUser: errs=", errs);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            console.log("user.js -> saveUser: return");
            return;
        }
        console.log("user.js ##########");

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentUser()[f.name]());

        try {
            let url = `${API_USER}/add`, method = 'POST';
            if (self.mode === 'edit' && self.currentUser().id && self.currentUser().id()) {
                url = `${API_USER}/${self.currentUser().id()}`;
                method = 'PUT';
            }
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.goUsers();
        } catch (err) { console.error('Save user error:', err); }
    };

    // Delete User
    self.deleteUser = async function(user) {
        console.log("user.js -> deleteUser: user=", user);
        if (!confirm('Are you sure?')) return;
        try {
            await fetch(`${API_USER}/${ko.unwrap(user.id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUsers();
        } catch (err) { console.error('Delete user error:', err); }
    };

    // Load User by ID
    self.loadUserById = async function(id) {
        console.log("user.js -> loadUserById: id=", id);
        try {
            const res = await fetch(`${API_USER}/${id}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) self.currentUser(new User(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load user error:', err); }
    };

    // ========================
    // Initialization
    // ========================
    console.log("Initialization block: called");
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editUserId');
        if (id) self.loadUserById(id);
    } else if (self.mode === 'add') {
        self.currentUser(new User({}, self.formConfig?.fields || []));
    } else {
        self.loadUsers();
    }

    // ========================
    // WRAPPER: MUST BE AT BOTTOM
    // ========================
    console.log("user.js -> Wrapper block: called");
    self.currentObject = self.currentUser;
    self.formTitle = self.mode === 'edit' ? 'Edit User' : 'Add User';

    self.goBack = function() {
        console.log("user.js Wrapper -> goBack called");
        return self.goUsers();
    };

    self.saveObject = function() {
        console.log("user.js Wrapper -> saveObject called");
        return self.saveUser();
    };

    self.goAddObject = function() {
        console.log("user.js Wrapper -> goAddObject called");
        return self.goAddUser();
    };

    self.objects = self.users;

    self.searchObjects = function() {
        console.log("user.js Wrapper -> searchObjects called");
        return self.searchUsers();
    };
}
