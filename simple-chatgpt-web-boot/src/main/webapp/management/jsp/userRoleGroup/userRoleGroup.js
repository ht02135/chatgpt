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
    self.mappings = ko.observableArray([]);
    self.currentMapping = ko.observable(new UserRoleGroup({}, self.formConfig?.fields || []));
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
    // Load Mappings
    // ========================
    self.loadMappings = async function() {
        console.log("userRoleGroup.js -> loadMappings called, mode=", self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            console.log("userRoleGroup.js -> loadMappings: qs=", qs);
            const res = await fetch(`${API_USER_ROLE_GROUP}/list?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("userRoleGroup.js -> loadMappings: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.mappings(paged.items.map(r => new UserRoleGroup(r, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.mappings([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load mappings error:', err);
            self.mappings([]);
            self.total(0);
        }
    };

    // ========================
    // Search & Reset
    // ========================
    self.searchUserRoleGroups = function() { self.page(1); self.loadMappings(); };
    self.resetUserRoleGroupSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadMappings();
    };

    // ========================
    // Pagination
    // ========================
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page()+1); self.loadMappings(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page()-1); self.loadMappings(); } };
    self.size.subscribe(() => { self.page(1); self.loadMappings(); });

    // ========================
    // Sorting
    // ========================
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadMappings();
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
        console.log("userRoleGroup.js -> invokeAction: action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // ========================
    // Save / Delete
    // ========================
    self.validateForm = function(obj, fields) { return self.validator ? self.validator.validateForm(obj, fields) : {}; };

    self.saveUserRoleGroup = async function() {
        console.log("userRoleGroup.js -> saveUserRoleGroup called, currentMapping=", ko.toJS(self.currentMapping()));
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentMapping(), self.formConfig.fields);
        console.log("userRoleGroup.js -> validation errs=", errs);
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentMapping());
        try {
            await fetch(`${API_USER_ROLE_GROUP}/add?userId=${encodeURIComponent(payload.userId)}&roleGroupId=${encodeURIComponent(payload.roleGroupId)}`, 
                        { method: 'POST', headers: { 'Content-Type':'application/json' } });
            self.loadMappings();
        } catch(err) { console.error('Save mapping error:', err); }
    };

    self.deleteUserRoleGroup = async function(mapping) {
        if (!confirm('Are you sure?')) return;
        try {
            const id = ko.unwrap(mapping.id);
            console.log("userRoleGroup.js -> deleteUserRoleGroup: id=", id);
            await fetch(`${API_USER_ROLE_GROUP}/remove?mappingId=${encodeURIComponent(id)}`, { method:'DELETE', headers:{'Accept':'application/json'} });
            self.loadMappings();
        } catch(err) { console.error('Delete mapping error:', err); }
    };

    // ========================
    // Initialization
    // ========================
    if (self.mode==='add') {
        self.currentMapping(new UserRoleGroup({}, self.formConfig?.fields || []));
    } else {
        self.loadMappings();
    }

    // Wrapper
    self.currentObject = self.currentMapping;
    self.objects = self.mappings;
}

// Export
export { UserRoleGroup, UserRoleGroupViewModel };
