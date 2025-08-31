// knockoutjs-mybatis-user/ext/user.js
// Knockout.js ViewModel for user management (paging, sorting, CRUD) for new JSP pages

function User(data) {
    this.id = ko.observable(data && data.id || 0);
    this.name = ko.observable(data && data.name || '');
    this.email = ko.observable(data && data.email || '');
    this.firstName = ko.observable(data && data.firstName || '');
    this.lastName = ko.observable(data && data.lastName || '');
    this.password = ko.observable(data && data.password || '');
    this.addressLine1 = ko.observable(data && data.addressLine1 || '');
    this.addressLine2 = ko.observable(data && data.addressLine2 || '');
    this.city = ko.observable(data && data.city || '');
    this.state = ko.observable(data && data.state || '');
    this.postCode = ko.observable(data && data.postCode || '');
    this.country = ko.observable(data && data.country || '');
}

function UserViewModel(params) {
    var self = this;
    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}));
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');
    self.mode = (params && params.mode) || 'list';
    const API_BASE = '/chatgpt/api/mybatis/users';

    // Load users with paging and sorting
    self.loadUsers = async function() {
        try {
            const res = await fetch(`${API_BASE}/paged?page=${self.page()}&size=${self.size()}&sortField=${self.sortField()}&sortOrder=${self.sortOrder()}`,
                { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS') {
                self.users((data.data.users || []).map(u => new User(u)));
                self.total(data.data.total || 0);
            } else {
                self.users([]);
                self.total(0);
            }
        } catch (err) {
            self.users([]);
            self.total(0);
        }
    };

    // Save or update user
    self.saveUser = async function() {
        const u = self.currentUser();
        const payload = {
            name: u.name(),
            email: u.email(),
            firstName: u.firstName(),
            lastName: u.lastName(),
            password: u.password(),
            addressLine1: u.addressLine1(),
            addressLine2: u.addressLine2(),
            city: u.city(),
            state: u.state(),
            postCode: u.postCode(),
            country: u.country()
        };
        if (self.mode === 'edit' && u.id() > 0) {
            payload.id = u.id();
        }
        try {
            let url = `${API_BASE}/add`;
            let method = 'POST';
            if (self.mode === 'edit' && u.id() > 0) {
                url = `${API_BASE}/${u.id()}`;
                method = 'PUT';
            }
            await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                body: JSON.stringify(payload)
            });
            self.goUsers();
        } catch (err) {}
    };

    self.deleteUser = async function(user) {
        if (!confirm('Are you sure you want to delete this user?')) return;
        try {
            await fetch(`${API_BASE}/${ko.unwrap(user.id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUsers();
        } catch (err) {}
    };

    // Navigation
    self.goUsers = function() { 
        // Always reload users list when returning to users.jsp
        window.location.href = 'users.jsp?reload=' + new Date().getTime(); 
    };
    self.goAddUser = function() { window.location.href = 'addUser.jsp'; };
    self.goEditUser = function(id) {
        // id may be an observable or a plain value
        var realId = ko.unwrap(id);
        localStorage.setItem('editUserId', realId);
        window.location.href = 'editUser.jsp';
    };

    // Paging/Sorting
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadUsers(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page() - 1); self.loadUsers(); } };
    self.maxPage = ko.computed(function() { return Math.ceil(self.total() / self.size()); });
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1); self.loadUsers();
    };
    self.size.subscribe(function() { self.page(1); self.loadUsers(); });

    // For edit page: load user
    self.loadEditUser = async function() {
        var id = localStorage.getItem('editUserId');
        if (self.mode === 'edit' && id) {
            try {
                const res = await fetch(`${API_BASE}/${id}`, { headers: { 'Accept': 'application/json' } });
                const data = await res.json();
                if (data.status === 'SUCCESS' && data.data) {
                    self.currentUser(new User(data.data));
                }
            } catch (err) {}
        }
    };

    // On page load, handle mode
    if (self.mode === 'edit') {
        // Only load if not already loaded
        if (!self.currentUser() || !self.currentUser().id() || self.currentUser().id() === 0) {
            self.loadEditUser();
        }
    } else if (self.mode === 'add') {
        self.currentUser(new User({}));
    } else {
        self.loadUsers();
    }
}