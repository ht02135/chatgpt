// =========================
// user.js
// Knockout.js ViewModel for Users
// =========================

function User(data, fields) {
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserViewModel(params, config) {
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;

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

    const API_USER = '/chatgpt/api/mybatis/users';

    // Build URLSearchParams for search
    self.buildSearchQuery = function() {
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
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_USER}/paged?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
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
    self.searchUsers = function() { self.page(1); self.loadUsers(); };
    self.resetSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadUsers();
    };

    // Pagination
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page()+1); self.loadUsers(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page()-1); self.loadUsers(); } };
    self.maxPage = ko.computed(() => Math.ceil(self.total() / self.size()));

    // Sorting
    self.setSort = function(field) {
        if(self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUsers();
    };

    self.size.subscribe(() => { self.page(1); self.loadUsers(); });

    // Navigation
    self.goUsers = function(){ window.location.href='users.jsp?reload='+new Date().getTime(); };
    self.goAddUser = function(){ window.location.href='addUser.jsp'; };
    self.goEditUser = function(id){ 
        localStorage.setItem('editUserId', ko.unwrap(id)); 
        window.location.href='editUser.jsp'; 
    };

    // ========================
    // Validation Helpers
    // ========================
    self.validateField = function(fieldName, value) {
        if (!self.validator) return '';
        return self.validator.validateField(fieldName, value);
    };

    self.validateForm = function(userObj, fieldsConfig) {
        if (!self.validator || !fieldsConfig) return {};
        const errors = {};
        fieldsConfig.forEach(f => {
            const val = ko.unwrap(userObj[f.name]);
            const err = self.validator.validateField(f.name, val);
            if (err) errors[f.name] = err;
        });
        return errors;
    };

    // Live Validation: subscribe to each field
    if (self.formConfig?.fields) {
        self.formConfig.fields.forEach(f => {
            if (self.currentUser()[f.name]) {
                self.currentUser()[f.name].subscribe(val => {
                    const err = self.validateField(f.name, val);
                    const allErrors = { ...self.errors() };
                    if (err) allErrors[f.name] = err;
                    else delete allErrors[f.name];
                    self.errors(allErrors);
                });
            }
        });
    }

    // Save User
    self.saveUser = async function() {
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentUser(), self.formConfig.fields);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentUser()[f.name]());

        try {
            let url = `${API_USER}/add`, method = 'POST';
            if (self.mode==='edit' && self.currentUser().id && self.currentUser().id()) { 
                url = `${API_USER}/${self.currentUser().id()}`; 
                method = 'PUT'; 
            }
            await fetch(url, { method, headers:{'Content-Type':'application/json'}, body:JSON.stringify(payload) });
            self.goUsers();
        } catch(err){ console.error('Save user error:', err); }
    };

    // Delete User
    self.deleteUser = async function(user){
        if(!confirm('Are you sure?')) return;
        try { 
            await fetch(`${API_USER}/${ko.unwrap(user.id)}`, { method:'DELETE', headers:{'Accept':'application/json'} }); 
            self.loadUsers(); 
        } catch(err){ console.error('Delete user error:', err); }
    };

    // Load User by ID
    self.loadUserById = async function(id){
        try{
            const res = await fetch(`${API_USER}/${id}`, { headers:{'Accept':'application/json'} });
            const data = await res.json();
            if(data.status==='SUCCESS' && data.data) self.currentUser(new User(data.data, self.formConfig?.fields||[]));
        } catch(err){ console.error('Load user error:', err); }
    };

    // ========================
    // Initialization
    // ========================
    if(self.mode==='edit'){
        const id = localStorage.getItem('editUserId');
        if(id) self.loadUserById(id);
    } else if(self.mode==='add') {
        self.currentUser(new User({}, self.formConfig?.fields||[]));
    } else {
        self.loadUsers();
    }
}
