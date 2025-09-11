// user.js

function User(data, fields) {
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserViewModel(config) {
    const self = this;
    self.gridConfig = config.grids.users;
    self.searchFormConfig = config.forms.searchUser;
    self.addFormConfig = config.forms.addUser;
    self.editFormConfig = config.forms.editUser;

    // Users
    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}, self.addFormConfig.fields));
    self.searchParams = {};
    self.searchFormConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    self.errors = ko.observable({});

    const API_USERS = '/chatgpt/api/mybatis/users';

    // Search
    self.searchUsers = async function() {
        const validator = new Validator(config.regex);
        const errs = validator.validateForm(self.searchFormConfig.fields, self.searchParams);
        self.errors(errs);
        if (Object.keys(errs).length > 0) return;

        const params = new URLSearchParams();
        Object.keys(self.searchParams).forEach(k => {
            const v = self.searchParams[k]();
            if (v && v.trim()) params.append(k, v.trim());
        });

        const res = await fetch(`${API_USERS}/paged?${params.toString()}`);
        const data = await res.json();
        if (data.status === 'SUCCESS') {
            self.users(data.data.users.map(u => new User(u, self.gridConfig.columns.map(c => ({name: c.name})))));
        } else self.users([]);
    };

    self.goAddUser = function() { window.location.href = 'addUser.jsp'; };
    self.goEditUser = function(u) {
        localStorage.setItem('editUserId', ko.unwrap(u.id));
        window.location.href = 'editUser.jsp';
    };
    self.deleteUser = async function(u) {
        if (!confirm('Delete this user?')) return;
        await fetch(`${API_USERS}/${ko.unwrap(u.id)}`, { method: 'DELETE' });
        self.searchUsers();
    };

    // Load user by ID
    self.loadUserById = async function(id) {
        const res = await fetch(`${API_USERS}/${id}`);
        const data = await res.json();
        if (data.status === 'SUCCESS') {
            self.currentUser(new User(data.data, self.editFormConfig.fields));
        }
    };

    // Save
    self.saveUser = async function(userData) {
        const validator = new Validator(config.regex);
        const formFields = userData.id ? self.editFormConfig.fields : self.addFormConfig.fields;
        const errs = validator.validateForm(formFields, userData);
        self.errors(errs);
        if (Object.keys(errs).length > 0) return;

        const url = userData.id ? `${API_USERS}/${userData.id}` : `${API_USERS}/add`;
        const method = userData.id ? 'PUT' : 'POST';

        await fetch(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(ko.toJS(userData)) });
        window.location.href = 'users.jsp';
    };
}
