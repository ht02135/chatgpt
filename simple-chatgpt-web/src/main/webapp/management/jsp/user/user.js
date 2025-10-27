// user.js

// const API_USER = '/chatgpt/api/management/users';
// detect context path dynamically from browser URL
const USER_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
// build API endpoint with the detected context path
const API_USER = `${USER_CONTEXT_PATH}/api/management/users`;

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

	/*
	?. is the optional chaining operator
	///////////////////
	equivalent ternary operator:
	self.mode = params.mode ? params.mode : 'list';
	*/
    self.mode = params.mode || 'list';
	/*
	?. is the optional chaining operator
	///////////////////
	equivalent ternary operator:
	self.gridConfig = (config !== null && typeof config !== 'undefined') ? config.grid : undefined;
	*/
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
	/*
	equivalent ternary operator:
	self.actionGroupMap = (config?.actionGroups !== null && typeof config?.actionGroups !== 'undefined') ? config.actionGroups : {};
	*/
    self.actionGroupMap = config?.actionGroups || {};
	console.log("user.js -> UserViewModel: #############");
	console.log('user.js -> UserViewModel: self.actionGroupMap=', self.actionGroupMap);
	console.log("user.js -> UserViewModel: #############");

    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig && self.searchConfig.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    // Pagination state
    //This is the current page number the user sees in the UI.
    self.page = ko.observable(1);    
    //This is the page size = how many items are shown per page.    
    self.size = ko.observable(10);
    //This is the total number of items available in the dataset.
    self.total = ko.observable(0);
    //This is the total number of pages. Calculated as ceil(total / size).
    self.maxPage = ko.computed(() => {
        const total = self.total() || 0;
        const size = self.size() || 1;
        return Math.max(1, Math.ceil(total / size));
    });
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

	// ========================
	// Helper: resolve sortField -> dbField
	// ========================
	self.resolveDbField = function(uiField) {
	    const col = self.gridConfig?.columns?.find(c => c.name === uiField);
	    return col?.dbField || uiField; // fallback to uiField if no mapping
	};
	
    // Build URLSearchParams for search
    self.buildSearchQuery = function() {
        console.log("user.js -> buildSearchQuery: called");
        const params = new URLSearchParams();
        params.append('page', self.page() - 1); // backend is 0-based
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

    // Load Users for Grid
    self.loadUsers = async function() {
        console.log("user.js -> loadUsers: called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            console.log("user.js -> loadUsers: qs=", qs);
            const res = await fetch(`${API_USER}?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("user.js -> loadUsers: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;

                console.log("user.js -> loadUsers: mapping items");
                self.users(paged.items.map(u => new User(u, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));

                console.log("user.js -> loadUsers: update pagination");
                paged.totalCount && self.total() !== paged.totalCount ? self.total(paged.totalCount) : null;
            } else {
                console.log("user.js -> loadUsers: empty result");
                self.users([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load users error:', err);
            self.users([]);
            self.total(0);
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
        console.log("user.js -> size.subscribe: called");
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

    // ========================
    // Navigation
    // ========================
    self.navigateToUsers = function() {
        console.log("user.js -> navigateToUsers: called");
        window.location.href = 'users.jsp?reload=' + new Date().getTime();
    };

    self.addUser = function() {
        console.log("user.js -> addUser: called");
        window.location.href = 'addUser.jsp';
    };

	self.editUser = function(user) {
		console.log("user.js -> editUser: #############");
		console.log("user.js -> editUser: user=", user);
		console.log("user.js -> editUser: ko.unwrap(user.id)=", ko.unwrap(user.id));
		console.log("user.js -> editUser: #############");

		if (!confirm('Are you sure?')) return;
		
	    localStorage.setItem('editUserId', ko.unwrap(user.id));
		console.log("user.js -> editUser: ##########");
	    window.location.href = 'editUser.jsp';
	};

    // ========================
    // Actions Resolver
    // ========================
    self.getActionsForColumn = function(column) {
        console.log("user.js -> getActionsForColumn: column=", column);
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

	self.invokeAction = function(action, row) {
	    console.log("user.js -> invokeAction called");
	    console.log("user.js -> invokeAction: action=", action);
	    console.log("user.js -> invokeAction: row=", row);
	    if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
	    	self[action.jsMethod](row);
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
        console.log("user.js -> saveUser: ##########");
        console.log("user.js -> saveUser: errs=", errs);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            console.log("user.js -> saveUser: return");
            return;
        }
        console.log("user.js -> saveUser: ##########");

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentUser()[f.name]());
        console.log("user.js -> saveUser: payload=", payload);

        try {
            // <-- fixed: use controller's /create and /update endpoints (keep method variable)
            let url = `${API_USER}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentUser().id && self.currentUser().id()) {
                const idVal = self.currentUser().id();
                // update uses query param id=...
                url = `${API_USER}/update?id=${encodeURIComponent(idVal)}`;
                method = 'PUT';
            }
            console.log("user.js -> saveUser: url=", url, "method=", method);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToUsers();
        } catch (err) { console.error('Save user error:', err); }
    };

    // Delete User
    self.deleteUser = async function(user) {
        console.log("user.js -> deleteUser: user=", user);
        if (!confirm('Are you sure?')) return;
        try {
            console.log("user.js -> deleteUser: id=", ko.unwrap(user.id));
            const idVal = ko.unwrap(user.id);
            // <-- fixed: call controller's delete endpoint with query param
            await fetch(`${API_USER}/delete?id=${encodeURIComponent(idVal)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUsers();
        } catch (err) { console.error('Delete user error:', err); }
    };

    // Load User by ID
    self.loadUserById = async function(id) {
        console.log("user.js -> loadUserById: id=", id);
        try {
            // <-- fixed: call /get?id=...
            const res = await fetch(`${API_USER}/get?id=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("user.js -> loadUserById: response=", data);
            if (data.status === 'SUCCESS' && data.data) self.currentUser(new User(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load user error:', err); }
    };

    // ========================
    // Initialization
    // ========================
    console.log("user.js -> Initialization block: called");
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editUserId');
        console.log("user.js -> Initialization: edit id=", id);
        if (id) self.loadUserById(id);
    } else if (self.mode === 'add') {
        console.log("user.js -> Initialization: add mode");
        self.currentUser(new User({}, self.formConfig?.fields || []));
    } else {
        console.log("user.js -> Initialization: list mode -> loadUsers");
        self.loadUsers();
    }
	
    // ========================
    // WRAPPER: MUST BE AT BOTTOM
    // ========================
    console.log("user.js -> Wrapper block: called");
    self.currentObject = self.currentUser;
    self.objects = self.users;
}

export { User, UserViewModel };