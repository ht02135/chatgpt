// pageRoleGroup.js

function PageRoleGroup(data, fields) {
    console.log("pageRoleGroup.js -> PageRoleGroup: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function PageRoleGroupViewModel(params, config) {
    console.log("pageRoleGroup.js -> PageRoleGroupViewModel: constructor called");
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
    self.pageRoleGroups = ko.observableArray([]);
    self.currentPageRoleGroup = ko.observable(new PageRoleGroup({}, self.formConfig?.fields || []));
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
    // CRUD OPERATIONS
    // ========================
    self.loadPageRoleGroups = async function() {
        console.log("pageRoleGroup.js -> loadPageRoleGroups called, mode=", self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const res = await fetch(`${API_PAGE_ROLE_GROUP}/search?${qs}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("pageRoleGroup.js -> loadPageRoleGroups response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.pageRoleGroups(paged.items.map(pg => new PageRoleGroup(pg, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                self.total(paged.totalCount || 0);
            } else {
                self.pageRoleGroups([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load pageRoleGroups error:', err);
            self.pageRoleGroups([]);
            self.total(0);
        }
    };

    self.loadPageRoleGroupById = async function(id) {
        console.log("pageRoleGroup.js -> loadPageRoleGroupById id=", id);
        try {
            const res = await fetch(`${API_PAGE_ROLE_GROUP}/get?id=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data)
                self.currentPageRoleGroup(new PageRoleGroup(data.data, self.formConfig?.fields || []));
        } catch (err) {
            console.error('Load pageRoleGroup error:', err);
        }
    };

    self.savePageRoleGroup = async function() {
        console.log("pageRoleGroup.js -> savePageRoleGroup called, currentPageRoleGroup=", ko.toJS(self.currentPageRoleGroup()));
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validator ? self.validator.validateForm(self.currentPageRoleGroup(), self.formConfig.fields) : {};
        if (Object.keys(errs).length > 0) { self.errors(errs); return; }

        const payload = ko.toJS(self.currentPageRoleGroup());
        try {
            let url = `${API_PAGE_ROLE_GROUP}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentPageRoleGroup().id && self.currentPageRoleGroup().id()) {
                url = `${API_PAGE_ROLE_GROUP}/update?id=${encodeURIComponent(self.currentPageRoleGroup().id())}`;
                method = 'PUT';
            }
            console.log("pageRoleGroup.js -> savePageRoleGroup: url=", url, "method=", method, "payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToPageRoleGroups();
        } catch (err) {
            console.error('Save pageRoleGroup error:', err);
        }
    };

    self.deletePageRoleGroup = async function(row) {
        if (!confirm('Are you sure you want to delete this page role group?')) return;
        try {
            const id = ko.unwrap(row.id);
            console.log("pageRoleGroup.js -> deletePageRoleGroup id=", id);
            await fetch(`${API_PAGE_ROLE_GROUP}/delete?id=${encodeURIComponent(id)}`, {
                method: 'DELETE',
                headers: { 'Accept': 'application/json' }
            });
            self.loadPageRoleGroups();
        } catch (err) {
            console.error('Delete pageRoleGroup error:', err);
        }
    };

    // ========================
    // Search / Pagination / Sort
    // ========================
    self.searchPageRoleGroups = function() { self.page(1); self.loadPageRoleGroups(); };
    self.resetPageRoleGroupSearch = function() {
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadPageRoleGroups();
    };
    self.nextPage = function() { if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadPageRoleGroups(); } };
    self.prevPage = function() { if (self.page() > 1) { self.page(self.page() - 1); self.loadPageRoleGroups(); } };
    self.size.subscribe(() => { self.page(1); self.loadPageRoleGroups(); });

    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadPageRoleGroups();
    };

    // ========================
    // Navigation
    // ========================
    self.navigateToPageRoleGroups = function() { window.location.href = 'pageRoleGroups.jsp'; };
    self.addPageRoleGroup = function() { window.location.href = 'addPageRoleGroup.jsp'; };
    self.editPageRoleGroup = function(row) {
        console.log("pageRoleGroup.js -> editPageRoleGroup row=", row);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editPageRoleGroupId', ko.unwrap(row.id));
        window.location.href = 'editPageRoleGroup.jsp';
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
        console.log("pageRoleGroup.js -> invokeAction action=", action, "row=", row);
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
        const editId = localStorage.getItem('editPageRoleGroupId');
        if (editId) self.loadPageRoleGroupById(editId);
    } else if (self.mode === 'add') {
        self.currentPageRoleGroup(new PageRoleGroup({}, self.formConfig?.fields || []));
    } else {
        self.loadPageRoleGroups();
    }

    // Wrapper (for generic binding)
    self.currentObject = self.currentPageRoleGroup;
    self.objects = self.pageRoleGroups;
}

// Export
export { PageRoleGroup, PageRoleGroupViewModel };
