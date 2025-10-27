// roleGroupRole.js

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
    // Load Role Group Roles
    // ========================
    self.loadRoleGroupRoles = async function() {
        console.log("roleGroupRoles.js -> loadRoleGroupRoles called, mode=", self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            console.log("roleGroupRoles.js -> loadRoleGroupRoles: qs=", qs);
            const res = await fetch(`${API_ROLE_GROUP_ROLE}/search?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("roleGroupRoles.js -> loadRoleGroupRoles: response=", data);

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
    // Search & Reset
    // ========================
    self.searchRoleGroupRoles = function() { self.page(1); self.loadRoleGroupRoles(); };
    self.resetRoleGroupRoleSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadRoleGroupRoles();
    };

    // ========================
    // Pagination
    // ========================
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page()+1); self.loadRoleGroupRoles(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page()-1); self.loadRoleGroupRoles(); } };
    self.size.subscribe(() => { self.page(1); self.loadRoleGroupRoles(); });

    // ========================
    // Sorting
    // ========================
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadRoleGroupRoles();
    };

    // ========================
    // Navigation
    // ========================
    self.navigateToRoleGroupRoles = function() { window.location.href = 'roleGroupRoles.jsp'; };
    self.addRoleToGroup = function() { window.location.href = 'addRoleGroupRole.jsp'; };

    self.editRoleGroupRole = function(roleGroupRole) {
        console.log("roleGroupRoles.js -> editRoleGroupRole: roleGroupRole=", roleGroupRole);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editRoleGroupRoleId', ko.unwrap(roleGroupRole.id));
        window.location.href = 'editRoleGroupRole.jsp';
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
        console.log("roleGroupRoles.js -> invokeAction: action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // ========================
    // Validation & Save
    // ========================
    self.validateForm = function(obj, fields) { return self.validator ? self.validator.validateForm(obj, fields) : {}; };

    self.saveRoleGroupRole = async function() {
        console.log("roleGroupRoles.js -> saveRoleGroupRole called, currentRoleGroupRole=", ko.toJS(self.currentRoleGroupRole()));
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentRoleGroupRole(), self.formConfig.fields);
        console.log("roleGroupRoles.js -> saveRoleGroupRole validation errs=", errs);
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentRoleGroupRole());
        try {
            let url = `${API_ROLE_GROUP_ROLE}/acreatedd`, method = 'POST';
            if (self.mode==='edit' && self.currentRoleGroupRole().id && self.currentRoleGroupRole().id()) {
                url = `${API_ROLE_GROUP_ROLE}/update?id=${encodeURIComponent(self.currentRoleGroupRole().roleGroupId())}&roleId=${encodeURIComponent(self.currentRoleGroupRole().roleId())}`;
                method = 'POST';
            }
            console.log("roleGroupRoles.js -> saveRoleGroupRole: url=", url, "method=", method, "payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type':'application/json' }, body: JSON.stringify(payload) });
            self.navigateToRoleGroupRoles();
        } catch(err) { console.error('Save roleGroupRole error:', err); }
    };

    self.deleteRoleGroupRole = async function(roleGroupRole) {
        if (!confirm('Are you sure you want to delete this mapping?')) return;
        try {
            const id = ko.unwrap(roleGroupRole.id);
            console.log("roleGroupRoles.js -> deleteRoleGroupRole: id=", id);
            await fetch(`${API_ROLE_GROUP_ROLE}/delete?id=${encodeURIComponent(id)}`, { method:'DELETE', headers:{'Accept':'application/json'} });
            self.loadRoleGroupRoles();
        } catch(err) { console.error('Delete roleGroupRole error:', err); }
    };

	self.loadRoleGroupRoleById = async function(id) {
	    console.log("roleGroupRoles.js -> loadRoleGroupRoleById id=", id);
	    try {
	        const res = await fetch(`${API_ROLE_GROUP_ROLE}/get?id=${encodeURIComponent(id)}`, { 
	            headers: { 'Accept': 'application/json' } 
	        });
	        const data = await res.json();
	        console.log("roleGroupRoles.js -> loadRoleGroupRoleById data=", data);

	        if (data.status === 'SUCCESS' && data.data) {
	            // ✅ Single object (not list)
	            const found = data.data;
	            self.currentRoleGroupRole(new RoleGroupRole(found, self.formConfig?.fields || []));
	        } else {
	            console.warn("roleGroupRoles.js -> loadRoleGroupRoleById: no data found or status not SUCCESS");
	        }
	    } catch (err) {
	        console.error('Load roleGroupRole error:', err);
	    }
	};

    // ========================
    // Initialization
    // ========================
    if (self.mode==='edit') {
        const editId = localStorage.getItem('editRoleGroupRoleId');
        if (editId) self.loadRoleGroupRoleById(parseInt(editId, 10));
    } else if (self.mode==='add') {
        self.currentRoleGroupRole(new RoleGroupRole({}, self.formConfig?.fields || []));
    } else {
        self.loadRoleGroupRoles();
    }

    // Wrapper
    self.currentObject = self.currentRoleGroupRole;
    self.objects = self.roleGroupRoles;
}

// Export
export { RoleGroupRole, RoleGroupRoleViewModel };
