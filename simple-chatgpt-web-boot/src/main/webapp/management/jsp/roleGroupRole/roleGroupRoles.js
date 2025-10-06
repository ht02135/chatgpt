// roleGroupRole.js

// detect context path dynamically from browser URL
const ROLE_GROUP_ROLE_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_ROLE_GROUP_ROLE = `${ROLE_GROUP_ROLE_CONTEXT_PATH}/api/management/rolegrouprolemappings`;

function RoleGroupRole(data, fields) {
    console.log("roleGroupRoles.js -> RoleGroupRole: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function RoleGroupRoleViewModel(params, config) {
    console.log("roleGroupRoles.js -> RoleGroupRoleViewModel: constructor called");
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
    self.roleGroupRoles = ko.observableArray([]);
    self.currentRoleGroupRole = ko.observable(new RoleGroupRole({}, self.formConfig?.fields || []));
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

    // ========================
    // Helpers
    // ========================
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
    // CRUD Operations
    // ========================
    self.loadRoleGroupRoles = async function() {
        console.log("roleGroupRoles.js -> loadRoleGroupRoles called, mode=", self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_ROLE_GROUP_ROLE}/searchMappings?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.roleGroupRoles(paged.items.map(r => new RoleGroupRole(r, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.roleGroupRoles([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load roleGroupRoles error:', err);
            self.roleGroupRoles([]);
            self.total(0);
        }
    };

    // ========================
    // Client-side navigation
    // ========================
    self.addRoleToGroup = function() { window.location.href = 'addRoleGroupRole.jsp'; };
    self.editRoleGroupRole = function(row) {
        localStorage.setItem('editRoleGroupRoleId', ko.unwrap(row.id));
        window.location.href = 'editRoleGroupRole.jsp';
    };

    // ========================
    // Delete
    // ========================
    self.deleteRoleGroupRole = async function(row) {
        if (!confirm('Are you sure you want to delete this mapping?')) return;
        try {
            const id = ko.unwrap(row.id);
            await fetch(`${API_ROLE_GROUP_ROLE}/deleteById?id=${encodeURIComponent(id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadRoleGroupRoles();
        } catch (err) {
            console.error('Delete roleGroupRole error:', err);
        }
    };

    // ========================
    // Save
    // ========================
    self.saveRoleGroupRole = async function() {
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validator ? self.validator.validateForm(self.currentRoleGroupRole(), self.formConfig.fields) : {};
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentRoleGroupRole());
        try {
            let url = `${API_ROLE_GROUP_ROLE}/add`, method = 'POST';
            if (self.mode === 'edit' && self.currentRoleGroupRole().id && self.currentRoleGroupRole().id()) {
                url = `${API_ROLE_GROUP_ROLE}/addIfNotExists?roleGroupId=${encodeURIComponent(self.currentRoleGroupRole().roleGroupId())}&roleId=${encodeURIComponent(self.currentRoleGroupRole().roleId())}`;
                method = 'POST';
            }
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToRoleGroupRoles();
        } catch (err) {
            console.error('Save roleGroupRole error:', err);
        }
    };

    // ========================
    // Load by ID
    // ========================
    self.loadRoleGroupRoleById = async function(id) {
        try {
            const res = await fetch(`${API_ROLE_GROUP_ROLE}/listAll`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) {
                const found = data.data.items.find(r => r.id === id);
                if (found) self.currentRoleGroupRole(new RoleGroupRole(found, self.formConfig?.fields || []));
            }
        } catch (err) { console.error('Load roleGroupRole error:', err); }
    };

    // ========================
    // Search, Pagination, Sort
    // ========================
    self.searchRoleGroupRoles = function() { self.page(1); self.loadRoleGroupRoles(); };
    self.resetRoleGroupRoleSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadRoleGroupRoles();
    };
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadRoleGroupRoles(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page() - 1); self.loadRoleGroupRoles(); } };
    self.size.subscribe(() => { self.page(1); self.loadRoleGroupRoles(); });
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadRoleGroupRoles();
    };

    // ========================
    // Navigation wrapper
    // ========================
    self.navigateToRoleGroupRoles = function() { window.location.href = 'roleGroupRoles.jsp'; };

    // ========================
    // Initialization
    // ========================
    if (self.mode === 'edit') {
        const editId = localStorage.getItem('editRoleGroupRoleId');
        if (editId) self.loadRoleGroupRoleById(parseInt(editId, 10));
    } else if (self.mode === 'add') {
        self.currentRoleGroupRole(new RoleGroupRole({}, self.formConfig?.fields || []));
    } else {
        self.loadRoleGroupRoles();
    }

    self.currentObject = self.currentRoleGroupRole;
    self.objects = self.roleGroupRoles;
}

export { RoleGroupRole, RoleGroupRoleViewModel };
