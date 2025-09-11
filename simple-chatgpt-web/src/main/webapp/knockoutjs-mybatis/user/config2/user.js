// user.js
function User(data, fields) {
    var self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserViewModel(params, config, regexes) {
    var self = this;
    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;

    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}, self.formConfig?.fields || []));
    self.searchParams = {};
    self.errors = ko.observable({});

    const validator = new Validator(regexes);

    // Init search params
    if (self.searchConfig && self.searchConfig.fields) {
        self.searchConfig.fields.forEach(f => { self.searchParams[f.name] = ko.observable(''); });
    }

    const API_USER = '/chatgpt/api/mybatis/users';

    // Search / Load Users
    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page());
        params.append('size', self.size());
        params.append('sortField', self.sortField());
        params.append('sortOrder', self.sortOrder());
        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = ko.unwrap(self.searchParams[f.name]);
                if (val && val.trim()) params.append(f.name, val.trim());
            });
        }
        return params.toString();
    };

    self.loadUsers = async function() {
        if (self.mode !== 'list') return;
        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_USER}/paged?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS') {
                self.users((data.data.users || []).map(u => new User(u, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                self.total(data.data.total || 0);
            } else {
                self.users([]);
                self.total(0);
            }
        } catch (err) { console.error(err); self.users([]); self.total(0); }
    };

    // Search
    self.searchUsers = function() {
        const errs = validator.validateForm(self.searchConfig.fields, self.searchParams);
        self.errors(errs);
        if (Object.keys(errs).length === 0) {
            self.page(1);
            self.loadUsers();
        }
    };

    self.resetSearch = function() {
        if (self.searchConfig?.fields) self.searchConfig.fields.forEach(f => self.searchParams[f.name](''));
        self.page(1); self.loadUsers();
    };

    // Save user
    self.saveUser = async function() {
        if (!self.formConfig) return;
        const user = self.currentUser();
        const errs = validator.validateForm(self.formConfig.fields, user);
        self.errors(errs);
        if (Object.keys(errs).length > 0) return;

        const payload = {};
        self.formConfig.fields.forEach(f => { payload[f.name] = ko.unwrap(user[f.name]); });

        let url = `${API_USER}/add`, method = 'POST';
        if (self.mode === 'edit' && user.id && user.id()) { url = `${API_USER}/${user.id()}`; method = 'PUT'; }

        try {
            await fetch(url, { method, headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify(payload) });
            self.goUsers();
        } catch (err) { console.error(err); }
    };

    self.deleteUser = async function(u) {
        if (!confirm('Are you sure?')) return;
        try { await fetch(`${API_USER}/${ko.unwrap(u.id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } }); self.loadUsers(); } 
        catch(err){ console.error(err); }
    };

    // Navigation
    self.goUsers = () => window.location.href='users.jsp';
    self.goAddUser = () => window.location.href='addUser.jsp';
    self.goEditUser = id => { localStorage.setItem('editUserId', ko.unwrap(id)); window.location.href='editUser.jsp'; };

    // Pagination / Sorting
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');
    self.nextPage = () => { if(self.page()<self.maxPage()) {self.page(self.page()+1); self.loadUsers();}};
    self.prevPage = () => { if(self.page()>1){self.page(self.page()-1); self.loadUsers(); }};
    self.maxPage = ko.computed(() => Math.ceil(self.total()/self.size()));
    self.setSort = field => { if(self.sortField()===field) self.sortOrder(self.sortOrder()==='ASC'?'DESC':'ASC'); else {self.sortField(field); self.sortOrder('ASC');} self.page(1); self.loadUsers();};
    self.size.subscribe(()=>{self.page(1); self.loadUsers();});

    self.loadUserById = async function(id){
        try{
            const res = await fetch(`${API_USER}/${id}`,{ headers:{ 'Accept':'application/json' }});
            const data = await res.json();
            if(data.status==='SUCCESS' && data.data) self.currentUser(new User(data.data, self.formConfig?.fields || []));
        }catch(err){ console.error(err);}
    };

    if(self.mode==='edit'){
        const editId=localStorage.getItem('editUserId');
        if(editId) self.loadUserById(editId);
    } else if(self.mode==='add'){
        self.currentUser(new User({}, self.formConfig?.fields || []));
    } else { self.loadUsers(); }
}
