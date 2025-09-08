// users.js
function User(data, fields) {
    var self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserViewModel(params, config) {
    var self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;

    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}, self.formConfig?.fields || []));

    // Search parameters - dynamically create based on search config
    self.searchParams = {};
    if (self.searchConfig && self.searchConfig.fields) {
        self.searchConfig.fields.forEach(field => {
            self.searchParams[field.name] = ko.observable('');
        });
    }

    // Pagination and sorting
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    const API_BASE = '/chatgpt/api/mybatis/users';

    // Build search query parameters
    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page());
        params.append('size', self.size());
        params.append('sortField', self.sortField());
        params.append('sortOrder', self.sortOrder());

        // Add search parameters if they have values
        if (self.searchConfig && self.searchConfig.fields) {
            self.searchConfig.fields.forEach(field => {
                const value = self.searchParams[field.name]();
                if (value && value.trim()) {
                    params.append(field.name, value.trim());
                }
            });
        }

        return params.toString();
    };

    // Load users for list page
    self.loadUsers = async function() {
        if (self.mode !== 'list') return;
        try {
            const queryString = self.buildSearchQuery();
            let url = `${API_BASE}/paged?${queryString}`;
            
            console.log("➡ Loading users with URL: ", url);
            
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            
            if (data.status === 'SUCCESS') {
                self.users((data.data.users || []).map(u => new User(u, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                self.total(data.data.total || 0);
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

    // Search users
    self.searchUsers = function() {
        console.log("➡ Performing search with params: ", ko.toJS(self.searchParams));
        self.page(1); // Reset to first page when searching
        self.loadUsers();
    };

    // Reset search
    self.resetSearch = function() {
        console.log("➡ Resetting search");
        if (self.searchConfig && self.searchConfig.fields) {
            self.searchConfig.fields.forEach(field => {
                self.searchParams[field.name]('');
            });
        }
        self.page(1);
        self.loadUsers();
    };

    // Save or update user for add/edit pages
    self.saveUser = async function() {
        if (!self.formConfig) return;

        const u = self.currentUser();
        const payload = {};
        self.formConfig.fields.forEach(f => {
            payload[f.name] = u[f.name]();
        });

        try {
            let url = `${API_BASE}/add`;
            let method = 'POST';
            if (self.mode === 'edit' && u.id && u.id()) {
                url = `${API_BASE}/${u.id()}`;
                method = 'PUT';
            }
            await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                body: JSON.stringify(payload)
            });
            self.goUsers();
        } catch (err) {
            console.error('Save user error:', err);
        }
    };

    self.deleteUser = async function(user) {
        if (!confirm('Are you sure you want to delete this user?')) return;
        try {
            await fetch(`${API_BASE}/${ko.unwrap(user.id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUsers();
        } catch (err) {
            console.error('Delete user error:', err);
        }
    };

    // Navigation
    self.goUsers = function() { window.location.href = 'users.jsp?reload=' + new Date().getTime(); };
    self.goAddUser = function() { window.location.href = 'addUser.jsp'; };
    self.goEditUser = function(id) {
        localStorage.setItem('editUserId', ko.unwrap(id));
        window.location.href = 'editUser.jsp';
    };

    // Pagination & Sorting
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadUsers(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page() - 1); self.loadUsers(); } };
    self.maxPage = ko.computed(() => Math.ceil(self.total() / self.size()));
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1); self.loadUsers();
    };
    self.size.subscribe(() => { self.page(1); self.loadUsers(); });

    // Load single user for edit page
    self.loadUserById = async function(id) {
        try {
            const res = await fetch(`${API_BASE}/${id}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) {
                self.currentUser(new User(data.data, self.formConfig?.fields || []));
            }
        } catch (err) {
            console.error('Load user error:', err);
        }
    };

    if (self.mode === 'edit') {
        var editId = localStorage.getItem('editUserId');
        if (editId) self.loadUserById(editId);
    } else if (self.mode === 'add') {
        self.currentUser(new User({}, self.formConfig?.fields || []));
    } else {
        self.loadUsers();
    }
}