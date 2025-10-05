// detect context path dynamically from browser URL
const ROLE_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_ROLE = `${ROLE_CONTEXT_PATH}/api/management/roles`;

function Role(data, fields) {
    console.log("role.js -> Role constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function RoleViewModel(params, config) {
    console.log("role.js -> RoleViewModel:", params, config);
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    console.log("role.js -> RoleViewModel: self.actionGroupMap=", self.actionGroupMap);

    self.roles = ko.observableArray([]);
    self.currentRole = ko.observable(new Role({}, self.formConfig?.fields || []));
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
        self.searchConfig?.fields?.forEach(f => {
            const val = self.searchParams[f.name]();
            if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
        });
        return params.toString();
    };

    self.loadRoles = async function() {
        if (self.mode !== 'list') return;
        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_ROLE}?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.roles(paged.items.map(r => new Role(r, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                paged.totalCount && self.total() !== paged.totalCount ? self.total(paged.totalCount) : null;
            } else {
                self.roles([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load roles error:', err);
            self.roles([]);
            self.total(0);
        }
    };

    // Search & Reset
    self.searchRoles = function() {
        self.page(1);
        self.loadRoles();
    };

    self.resetRoleSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadRoles();
    };

    // Pagination
    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.loadRoles();
        }
    };

    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.loadRoles();
        }
    };

    self.size.subscribe(() => {
        self.page(1);
        self.loadRoles();
    });

    // Sorting
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadRoles();
    };

    // Navigation
    self.navigateToRoles = function() {
        window.location.href = 'roles.jsp?reload=' + new Date().getTime();
    };

    self.addRole = function() {
        window.location.href = 'addRole.jsp';
    };

    self.editRole = function(role) {
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editRoleId', ko.unwrap(role.id));
        window.location.href = 'editRole.jsp';
    };

    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

    self.validateForm = function(roleObj, fieldsConfig) {
        return self.validator ? self.validator.validateForm(roleObj, fieldsConfig) : {};
    };

    self.saveRole = async function() {
        if (!self.formConfig) return;
        self.errors({});
        const errs = self.validateForm(self.currentRole(), self.formConfig.fields);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }
        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentRole()[f.name]());

        try {
            let url = `${API_ROLE}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentRole().id && self.currentRole().id()) {
                const idVal = self.currentRole().id();
                url = `${API_ROLE}/update?id=${encodeURIComponent(idVal)}`;
                method = 'PUT';
            }
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToRoles();
        } catch (err) {
            console.error('Save role error:', err);
        }
    };

    self.deleteRole = async function(role) {
        if (!confirm('Are you sure?')) return;
        try {
            const idVal = ko.unwrap(role.id);
            await fetch(`${API_ROLE}/delete?id=${encodeURIComponent(idVal)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadRoles();
        } catch (err) {
            console.error('Delete role error:', err);
        }
    };

    self.loadRoleById = async function(id) {
        try {
            const res = await fetch(`${API_ROLE}/get?id=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) self.currentRole(new Role(data.data, self.formConfig?.fields || []));
        } catch (err) {
            console.error('Load role error:', err);
        }
    };

    // Initialization
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editRoleId');
        if (id) self.loadRoleById(id);
    } else if (self.mode === 'add') {
        self.currentRole(new Role({}, self.formConfig?.fields || []));
    } else {
        self.loadRoles();
    }

    self.currentObject = self.currentRole;
    self.objects = self.roles;
}
