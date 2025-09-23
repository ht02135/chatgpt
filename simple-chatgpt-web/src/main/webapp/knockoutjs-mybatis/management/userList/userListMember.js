// userListMember.js

const API_USERLIST_MEMBER = '/chatgpt/api/management/userlistmembers';

function UserListMember(data, fields) {
    console.log("userListMember.js -> UserListMember: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserListMemberViewModel(mode, config, userListId) {
    const self = this;

    console.log("userListMember.js -> UserListMemberViewModel: constructor called");

    // ========================
    // Mode & Configs
    // ========================
    self.mode = mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};
    self.userListId = userListId || localStorage.getItem('editUserListId');

    // ========================
    // Observables
    // ========================
    self.members = ko.observableArray([]);
    self.currentMember = ko.observable(new UserListMember({}, self.formConfig?.fields || []));
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

    // Build query string with userListId
    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page() - 1);
        params.append('size', self.size());
        params.append('sortField', self.resolveDbField(self.sortField()));
        params.append('sortDirection', self.sortOrder());
        params.append('userListId', self.userListId);

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = self.searchParams[f.name]();
                if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
            });
        }
        return params.toString();
    };

    // ========================
    // Load Members
    // ========================
    self.loadUserListMembers = async function() {
        console.log("userListMember.js -> loadUserListMembers called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_USERLIST_MEMBER}/search?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.members(paged.items.map(m => new UserListMember(m, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.members([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load members error:', err);
            self.members([]);
            self.total(0);
        }
    };

    // ========================
    // Search & Reset
    // ========================
    self.searchUserListMembers = function() {
        console.log("userListMember.js -> searchUserListMembers called");
        self.page(1);
        self.loadUserListMembers();
    };
    self.resetSearch = function() {
        console.log("userListMember.js -> resetSearch called");
        self.searchParams && Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadUserListMembers();
    };

    // ========================
    // Pagination
    // ========================
    self.nextPage = function() {
        if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadUserListMembers(); }
    };
    self.prevPage = function() {
        if (self.page() > 1) { self.page(self.page() - 1); self.loadUserListMembers(); }
    };
    self.size.subscribe(() => { self.page(1); self.loadUserListMembers(); });

    // ========================
    // Sorting
    // ========================
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUserListMembers();
    };

    // ========================
    // Navigation
    // ========================
    self.navigateToMembers = function() { window.location.href = 'editUserList.jsp'; };
    self.addUserListMember = function() { window.location.href = 'addUserListMember.jsp'; };
    self.editUserListMember = function(id) {
        localStorage.setItem('editUserListMemberId', ko.unwrap(id));
        window.location.href = 'editUserListMember.jsp';
    };

    // ========================
    // Action Resolver
    // ========================
    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const group = self.actionGroupMap[column.actions];
        return Array.isArray(group) ? group.filter(a => a.visible !== false) : [];
    };
    self.invokeAction = function(action, row) {
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            if (/^edit(UserListMember|Object)$/.test(action.jsMethod)) self[action.jsMethod](ko.unwrap(row.id));
            else self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // ========================
    // Validation & Save
    // ========================
    self.validateForm = function(obj, fields) {
        return self.validator ? self.validator.validateForm(obj, fields) : {};
    };

    self.saveUserListMember = async function() {
        console.log("userListMember.js -> saveUserListMember called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentMember(), self.formConfig.fields);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        try {
            const payload = {
                ...ko.toJS(self.currentMember()),
                userListId: self.userListId || localStorage.getItem('editUserListId')
            };

            let url = `${API_USERLIST_MEMBER}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentMember().id && self.currentMember().id()) {
                url = `${API_USERLIST_MEMBER}/update?id=${encodeURIComponent(self.currentMember().id())}`;
                method = 'PUT';
            }

            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToMembers();
        } catch (err) { console.error('Save member error:', err); }
    };

    // ========================
    // Delete
    // ========================
    self.deleteUserListMember = async function(row) {
        if (!confirm('Are you sure?')) return;
        try {
            await fetch(`${API_USERLIST_MEMBER}/delete?memberId=${encodeURIComponent(ko.unwrap(row.id))}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUserListMembers();
        } catch (err) { console.error('Delete member error:', err); }
    };

    // ========================
    // Load by ID
    // ========================
    self.loadUserListMemberById = async function(id) {
        try {
            const res = await fetch(`${API_USERLIST_MEMBER}/get?memberId=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) self.currentMember(new UserListMember(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load member error:', err); }
    };

    // ========================
    // Initialization block
    // ========================
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editUserListMemberId');
        if (id) self.loadUserListMemberById(id);
    } else if (self.mode === 'add') {
        self.currentMember(new UserListMember({}, self.formConfig?.fields || []));
    } else {
        self.loadUserListMembers();
    }
	
    // ========================
    // Wrapper block
    // ========================
    self.currentObject = self.currentMember;
    self.objects = self.members;

    self.navigateToObjects = function() { return self.navigateToMembers(); };
    self.saveObject = function() { return self.saveUserListMember(); };
    self.addObject = function() { return self.addUserListMember(); };
    self.searchObjects = function() { return self.searchUserListMembers(); };
}

export default UserListMemberViewModel;
