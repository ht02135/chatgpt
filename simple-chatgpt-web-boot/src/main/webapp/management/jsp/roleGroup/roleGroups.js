// roleGroup.js

function RoleGroup(data, fields) {
    console.log("roleGroups.js -> RoleGroup: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function RoleGroupViewModel(params, config) {
    console.log("roleGroups.js -> RoleGroupViewModel: constructor called");
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
    self.roleGroups = ko.observableArray([]);
    self.currentRoleGroup = ko.observable(new RoleGroup({}, self.formConfig?.fields || []));
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
    // Load Role Groups
    // ========================
    self.loadRoleGroups = async function() {
        console.log("roleGroups.js -> loadRoleGroups called, mode=", self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            console.log("roleGroups.js -> loadRoleGroups: qs=", qs);
            const res = await fetch(`${API_ROLE_GROUP}/search?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("roleGroups.js -> loadRoleGroups: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.roleGroups(paged.items.map(rg => new RoleGroup(rg, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.roleGroups([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load role groups error:', err);
            self.roleGroups([]);
            self.total(0);
        }
    };

    // ========================
    // Search & Reset
    // ========================
    self.searchRoleGroups = function() { self.page(1); self.loadRoleGroups(); };
    self.resetRoleGroupSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadRoleGroups();
    };

    // ========================
    // Pagination
    // ========================
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page()+1); self.loadRoleGroups(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page()-1); self.loadRoleGroups(); } };
    self.size.subscribe(() => { self.page(1); self.loadRoleGroups(); });

    // ========================
    // Sorting
    // ========================
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadRoleGroups();
    };

    // ========================
    // Navigation
    // ========================
    self.navigateToRoleGroups = function() { window.location.href = 'roleGroups.jsp'; };
    self.addRoleGroup = function() { window.location.href = 'addRoleGroup.jsp'; };

    self.editRoleGroup = function(roleGroup) {
        console.log("roleGroups.js -> editRoleGroup: roleGroup=", roleGroup);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editRoleGroupId', ko.unwrap(roleGroup.id));
        window.location.href = 'editRoleGroup.jsp';
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
        console.log("roleGroups.js -> invokeAction: action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // ========================
    // Validation & Save
    // ========================
    self.validateForm = function(obj, fields) { return self.validator ? self.validator.validateForm(obj, fields) : {}; };

    self.saveRoleGroup = async function() {
        console.log("roleGroups.js -> saveRoleGroup called, currentRoleGroup=", ko.toJS(self.currentRoleGroup()));
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentRoleGroup(), self.formConfig.fields);
        console.log("roleGroups.js -> saveRoleGroup validation errs=", errs);
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentRoleGroup());
        try {
            let url = `${API_ROLE_GROUP}/create?`, method='POST';
            if (self.mode==='edit' && self.currentRoleGroup().id && self.currentRoleGroup().id()) {
                url = `${API_ROLE_GROUP}/update?id=${encodeURIComponent(self.currentRoleGroup().id())}`;
                method = 'PUT';
            }
            console.log("roleGroups.js -> saveRoleGroup: url=", url, "method=", method, "payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type':'application/json' }, body: JSON.stringify(payload) });
            self.navigateToRoleGroups();
        } catch(err) { console.error('Save role group error:', err); }
    };

    self.deleteRoleGroup = async function(roleGroup) {
        if (!confirm('Are you sure?')) return;
        try {
            const id = ko.unwrap(roleGroup.id);
            console.log("roleGroups.js -> deleteRoleGroup: id=", id);
            await fetch(`${API_ROLE_GROUP}/delete?id=${encodeURIComponent(id)}`, { method:'DELETE', headers:{'Accept':'application/json'} });
            self.loadRoleGroups();
        } catch(err) { console.error('Delete role group error:', err); }
    };

    self.loadRoleGroupById = async function(id) {
        console.log("roleGroups.js -> loadRoleGroupById id=", id);
        try {
            const res = await fetch(`${API_ROLE_GROUP}/get?id=${encodeURIComponent(id)}`, { headers:{'Accept':'application/json'} });
            const data = await res.json();
            console.log("roleGroups.js -> loadRoleGroupById data=", data);
            if (data.status==='SUCCESS' && data.data) self.currentRoleGroup(new RoleGroup(data.data, self.formConfig?.fields || []));
        } catch(err) { console.error('Load role group error:', err); }
    };

    // ========================
    // Initialization
    // ========================
    if (self.mode==='edit') {
        const editId = localStorage.getItem('editRoleGroupId');
        if (editId) self.loadRoleGroupById(editId);
    } else if (self.mode==='add') {
        self.currentRoleGroup(new RoleGroup({}, self.formConfig?.fields || []));
    } else {
        self.loadRoleGroups();
    }

    // Wrapper
    self.currentObject = self.currentRoleGroup;
    self.objects = self.roleGroups;
}

// Export
export { RoleGroup, RoleGroupViewModel };
