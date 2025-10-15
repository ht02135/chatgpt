// userRoleGroup.js

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

    // ========================
    // Mode & Configs
    // ========================
    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    // ========================
    // Observables
    // ========================
    self.userRoleGroups = ko.observableArray([]);
    self.currentUserRoleGroup = ko.observable(new UserRoleGroup({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    // ========================
    // Pagination / Sorting
    // ========================
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.maxPage = ko.computed(() => Math.max(1, Math.ceil((self.total() || 0) / (self.size() || 1))));
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    // ========================
    // Helpers
    // ========================
    self.resolveDbField = function(uiField) {
        const col = self.gridConfig?.columns?.find(c => c.name === uiField);
        return col?.dbField || uiField;
    };

    // ========================
    // Build Search Query
    // ========================
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
    // CRUD METHODS
    // ========================

    self.loadUserRoleGroups = async function() {
        console.log("userRoleGroup.js -> loadUserRoleGroups called, mode=", self.mode);
        if (self.mode !== 'list') return;
        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_USER_ROLE_GROUP}/search?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("userRoleGroup.js -> loadUserRoleGroups response=", data);
            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.userRoleGroups(paged.items.map(r => new UserRoleGroup(r, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                self.total(paged.totalCount || 0);
            } else {
                self.userRoleGroups([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load userRoleGroups error:', err);
            self.userRoleGroups([]);
            self.total(0);
        }
    };

    self.loadUserRoleGroupById = async function(id) {
        console.log("userRoleGroup.js -> loadUserRoleGroupById id=", id);
        try {
            const res = await fetch(`${API_USER_ROLE_GROUP}/get?id=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("userRoleGroup.js -> loadUserRoleGroupById data=", data);
            if (data.status === 'SUCCESS' && data.data)
                self.currentUserRoleGroup(new UserRoleGroup(data.data, self.formConfig?.fields || []));
        } catch (err) {
            console.error('Load userRoleGroupById error:', err);
        }
    };

    self.saveUserRoleGroup = async function() {
        console.log("userRoleGroup.js -> saveUserRoleGroup called, currentUserRoleGroup=", ko.toJS(self.currentUserRoleGroup()));
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validator ? self.validator.validateForm(self.currentUserRoleGroup(), self.formConfig.fields) : {};
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentUserRoleGroup());
        try {
            let url = `${API_USER_ROLE_GROUP}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentUserRoleGroup().id && self.currentUserRoleGroup().id()) {
                url = `${API_USER_ROLE_GROUP}/update?id=${encodeURIComponent(self.currentUserRoleGroup().id())}`;
                method = 'PUT';
            }
            console.log("userRoleGroup.js -> saveUserRoleGroup: url=", url, "method=", method, "payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToUserRoleGroups();
        } catch (err) {
            console.error('Save userRoleGroup error:', err);
        }
    };

    self.deleteUserRoleGroup = async function(row) {
        if (!confirm('Are you sure you want to delete this user role group?')) return;
        try {
            const id = ko.unwrap(row.id);
            console.log("userRoleGroup.js -> deleteUserRoleGroup id=", id);
            await fetch(`${API_USER_ROLE_GROUP}/delete?id=${encodeURIComponent(id)}`, {
                method: 'DELETE',
                headers: { 'Accept': 'application/json' }
            });
            self.loadUserRoleGroups();
        } catch (err) {
            console.error('Delete userRoleGroup error:', err);
        }
    };

    // ========================
    // Search / Pagination / Sort
    // ========================
    self.searchUserRoleGroups = function() { self.page(1); self.loadUserRoleGroups(); };
    self.resetUserRoleGroupSearch = function() {
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
    // Navigation
    // ========================
    self.navigateToUserRoleGroups = function() { window.location.href = 'userRoleGroups.jsp'; };
    self.addUserRoleGroup = function() { window.location.href = 'addUserRoleGroup.jsp'; };
    self.editUserRoleGroup = function(row) {
        console.log("userRoleGroup.js -> editUserRoleGroup row=", row);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editUserRoleGroupId', ko.unwrap(row.id));
        window.location.href = 'editUserRoleGroup.jsp';
    };

    // ========================
    // Actions
    // ========================
    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const group = self.actionGroupMap[column.actions];
        return Array.isArray(group) ? group.filter(a => a.visible !== false) : [];
    };
    self.invokeAction = function(action, row) {
        console.log("userRoleGroup.js -> invokeAction action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

    // ========================
    // Initialization
    // ========================
    if (self.mode === 'edit') {
        const editId = localStorage.getItem('editUserRoleGroupId');
        if (editId) self.loadUserRoleGroupById(editId);
    } else if (self.mode === 'add') {
        self.currentUserRoleGroup(new UserRoleGroup({}, self.formConfig?.fields || []));
    } else {
        self.loadUserRoleGroups();
    }

    // Wrapper (for generic binding)
    self.currentObject = self.currentUserRoleGroup;
    self.objects = self.userRoleGroups;
}

// Export
export { UserRoleGroup, UserRoleGroupViewModel };
