// userRoleGroup.js

// detect context path dynamically from browser URL
const USER_ROLE_GROUP_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_USER_ROLE_GROUP = `${USER_ROLE_GROUP_CONTEXT_PATH}/api/management/userrolegroups`;

function UserRoleGroup(data, fields) {
    console.log("userRoleGroup.js -> UserRoleGroup: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserRoleGroupViewModel(params, config) {
    console.log("userRoleGroup.js -> UserRoleGroupViewModel: constructor called");
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    self.userRoleGroups = ko.observableArray([]);
    self.currentUserRoleGroup = ko.observable(new UserRoleGroup({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.maxPage = ko.computed(() => Math.max(1, Math.ceil((self.total() || 0) / (self.size() || 1))));
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    self.resolveDbField = function(uiField) {
        const col = self.gridConfig?.columns?.find(c => c.name === uiField);
        return col?.dbField || uiField;
    };

    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page() - 1);
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

    // ========================
    // Load
    // ========================
    self.loadUserRoleGroups = async function() {
        console.log("userRoleGroup.js -> loadUserRoleGroups called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            console.log("userRoleGroup.js -> loadUserRoleGroups: query string=", qs);
            const res = await fetch(`${API_USER_ROLE_GROUP}/findUserRoleGroups?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("userRoleGroup.js -> loadUserRoleGroups: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.userRoleGroups(paged.items.map(r => new UserRoleGroup(r, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.userRoleGroups([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load user role groups error:', err);
            self.userRoleGroups([]);
            self.total(0);
        }
    };

    // ========================
    // Save
    // ========================
    self.saveUserRoleGroup = async function() {
        console.log("userRoleGroup.js -> saveUserRoleGroup called, currentUserRoleGroup=", ko.toJS(self.currentUserRoleGroup()));
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validator ? self.validator.validateForm(self.currentUserRoleGroup(), self.formConfig.fields) : {};
        console.log("userRoleGroup.js -> saveUserRoleGroup validation errs=", errs);
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentUserRoleGroup());
        try {
            let url = `${API_USER_ROLE_GROUP}/insertUserRoleGroup?userId=${encodeURIComponent(payload.userId)}&roleGroupId=${encodeURIComponent(payload.roleGroupId)}`;
            let method = 'POST';
            if (self.mode === 'edit' && payload.id) {
                url = `${API_USER_ROLE_GROUP}/updateUserRoleGroup?id=${encodeURIComponent(payload.id)}&userId=${encodeURIComponent(payload.userId)}&roleGroupId=${encodeURIComponent(payload.roleGroupId)}`;
                method = 'PUT';
            }
            console.log("userRoleGroup.js -> saveUserRoleGroup: url=", url, "method=", method, "payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' } });
            self.loadUserRoleGroups();
        } catch (err) {
            console.error('Save user role group error:', err);
        }
    };
	
	// ========================
	// Navigation
	// ========================
	self.navigateToUserRoleGroups = function() {
	    console.log("userRoleGroup.js -> navigateToUserRoleGroups called");
	    window.location.href = 'userRoleGroups.jsp'; // adjust URL if different
	};
	
    // ========================
    // Delete
    // ========================
    self.deleteUserRoleGroupById = async function(row) {
        if (!confirm('Are you sure?')) return;
        try {
            const id = ko.unwrap(row.id);
            console.log("userRoleGroup.js -> deleteUserRoleGroupById id=", id);
            await fetch(`${API_USER_ROLE_GROUP}/deleteUserRoleGroupById?id=${encodeURIComponent(id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUserRoleGroups();
        } catch (err) {
            console.error('Delete user role group by ID error:', err);
        }
    };

    self.deleteUserRoleGroupByUserAndGroup = async function(row) {
        if (!confirm('Are you sure?')) return;
        try {
            const userId = ko.unwrap(row.userId);
            const roleGroupId = ko.unwrap(row.roleGroupId);
            console.log("userRoleGroup.js -> deleteUserRoleGroupByUserAndGroup userId=", userId, "roleGroupId=", roleGroupId);
            await fetch(`${API_USER_ROLE_GROUP}/deleteUserRoleGroupByUserAndGroup?userId=${encodeURIComponent(userId)}&roleGroupId=${encodeURIComponent(roleGroupId)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUserRoleGroups();
        } catch (err) {
            console.error('Delete user role group by user & group error:', err);
        }
    };

    // ========================
    // Load by User or RoleGroup ID
    // ========================
    self.loadByUserId = async function(userId) {
        console.log("userRoleGroup.js -> loadByUserId userId=", userId);
        const res = await fetch(`${API_USER_ROLE_GROUP}/findByUserId?userId=${encodeURIComponent(userId)}`, { headers: { 'Accept': 'application/json' } });
        const data = await res.json();
        console.log("userRoleGroup.js -> loadByUserId response=", data);
    };

    self.loadByRoleGroupId = async function(roleGroupId) {
        console.log("userRoleGroup.js -> loadByRoleGroupId roleGroupId=", roleGroupId);
        const res = await fetch(`${API_USER_ROLE_GROUP}/findByRoleGroupId?roleGroupId=${encodeURIComponent(roleGroupId)}`, { headers: { 'Accept': 'application/json' } });
        const data = await res.json();
        console.log("userRoleGroup.js -> loadByRoleGroupId response=", data);
    };

    // ========================
    // Search / Pagination / Sorting
    // ========================
    self.searchUserRoleGroups = function() { self.page(1); self.loadUserRoleGroups(); };
    self.resetSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadUserRoleGroups();
    };

    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadUserRoleGroups(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page() - 1); self.loadUserRoleGroups(); } };
    self.size.subscribe(() => { self.page(1); self.loadUserRoleGroups(); });

    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUserRoleGroups();
    };

    // ========================
    // Init
    // ========================
    if (self.mode === 'edit') {
        const editId = localStorage.getItem('editUserRoleGroupId');
        if (editId) self.loadByUserId(editId);
    } else {
        self.loadUserRoleGroups();
    }

    self.currentObject = self.currentUserRoleGroup;
    self.objects = self.userRoleGroups;
}

export { UserRoleGroup, UserRoleGroupViewModel };
