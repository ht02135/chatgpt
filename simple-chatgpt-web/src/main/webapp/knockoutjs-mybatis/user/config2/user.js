// user.js

function User(data, fields) {
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserViewModel(config) {
    const self = this;

    self.gridConfig = config.grid;
    self.formConfig = config.form; // Add/Edit form
    self.searchConfig = config.search;

    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}, self.formConfig?.fields || []));

    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => {
            self.searchParams[f.name] = ko.observable('');
        });
    }

    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    const API_BASE = '/chatgpt/api/mybatis/users';

    // --- Users List ---
    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page());
        params.append('size', self.size());
        params.append('sortField', self.sortField());
        params.append('sortOrder', self.sortOrder());

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const value = self.searchParams[f.name]();
                if (value && value.trim()) params.append(f.name, value.trim());
            });
        }

        return params.toString();
    };

    self.loadUsers = async function() {
        try {
            const query = self.buildSearchQuery();
            const url = `${API_BASE}/paged?${query}`;
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

    self.searchUsers = function() {
        self.page(1);
        self.loadUsers();
    };

    self.resetSearch = function() {
        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => self.searchParams[f.name](''));
        }
        self.page(1);
        self.loadUsers();
    };

    // --- Add/Edit ---
    self.saveUser = async function(formData) {
        try {
            let url = `${API_BASE}/add`;
            let method = 'POST';
            if (formData.id) {
                url = `${API_BASE}/${formData.id}`;
                method = 'PUT';
            }
            await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                body: JSON.stringify(formData)
            });
            window.location.href = 'users.jsp';
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

    self.goAddUser = function() { window.location.href = 'addUser.jsp'; };
    self.goUsers = function() { window.location.href = 'users.jsp'; };
    self.goEditUser = function(id) {
        localStorage.setItem('editUserId', ko.unwrap(id));
        window.location.href = 'editUser.jsp';
    };

    // --- Pagination & Sorting ---
    self.nextPage = function() { if (self.page() < self.maxPage()) self.page(self.page()+1); self.loadUsers(); };
    self.prevPage = function() { if (self.page() > 1) self.page(self.page()-1); self.loadUsers(); };
    self.maxPage = ko.computed(() => Math.ceil(self.total() / self.size()));
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1); self.loadUsers();
    };
    self.size.subscribe(() => { self.page(1); self.loadUsers(); });

    // --- Load user for edit ---
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
}
