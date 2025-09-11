// user.js
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
    self.errors = ko.observable({});

    // Search params
    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    // Pagination and sorting
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    const API_BASE = '/chatgpt/api/mybatis/users';

    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page());
        params.append('size', self.size());
        params.append('sortField', self.sortField());
        params.append('sortOrder', self.sortOrder());

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = self.searchParams[f.name]();
                if (val && val.trim()) params.append(f.name, val.trim());
            });
        }
        return params.toString();
    };

    self.loadUsers = async function() {
        if (self.mode !== 'list') return;
        try {
            const url = `${API_BASE}/paged?${self.buildSearchQuery()}`;
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS') {
                const fields = self.gridConfig?.columns.map(c => ({ name: c.name })) || [];
                self.users((data.data.users || []).map(u => new User(u, fields)));
                self.total(data.data.total || 0);
            } else {
                self.users([]); self.total(0);
            }
        } catch (err) {
            console.error(err);
            self.users([]); self.total(0);
        }
    };

    self.searchUsers = function() { self.page(1); self.loadUsers(); };
    self.resetSearch = function() {
        if (self.searchConfig?.fields) self.searchConfig.fields.forEach(f => self.searchParams[f.name](''));
        self.page(1); self.loadUsers();
    };

    self.saveUser = async function() {
        if (!self.formConfig) return;
        const u = self.currentUser();
        const validator = self.validator || new Validator([]);
        const { isValid, errors } = validator.validateForm(u, self.formConfig.fields);
        self.errors(errors);

        if (!isValid) return;

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = ko.isObservable(u[f.name]) ? u[f.name]() : u[f.name]);

        try {
            let url = `${API_BASE}/add`;
            let method = 'POST';
            if (self.mode === 'edit' && u.id && u.id()) { url = `${API_BASE}/${u.id()}`; method = 'PUT'; }
            await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify(payload) });
            self.goUsers();
        } catch (err) { console.error(err); }
    };

    self.deleteUser = async function(user) {
        if (!confirm('Are you sure?')) return;
        try {
            await fetch(`${API_BASE}/${ko.unwrap(user.id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUsers();
        } catch (err) { console.error(err); }
    };

    self.goUsers = function() { window.location.href = 'users.jsp?reload=' + new Date().getTime(); };
    self.goAddUser = function() { window.location.href = 'addUser.jsp'; };
    self.goEditUser = function(id) { localStorage.setItem('editUserId', ko.unwrap(id)); window.location.href = 'editUser.jsp'; };

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
            if (data.status === 'SUCCESS' && data.data) self.currentUser(new User(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error(err); }
    };

    // Initialize
    if (self.mode === 'edit') {
        (async () => {
            const regexConfig = await configLoader.getRegexConfig();
            self.validator = new Validator(regexConfig);
            self.errors(ko.observable({}));
            const editId = localStorage.getItem('editUserId');
            if (editId) await self.loadUserById(editId);
        })();
    } else if (self.mode === 'add') {
        (async () => {
            const regexConfig = await configLoader.getRegexConfig();
            self.validator = new Validator(regexConfig);
            self.errors(ko.observable({}));
            self.currentUser(new User({}, self.formConfig?.fields || []));
        })();
    } else {
        self.loadUsers();
    }
}
