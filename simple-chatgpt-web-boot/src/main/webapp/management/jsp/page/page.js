// page.js

function Page(data, fields) {
    console.log("page.js -> Page: called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function PageViewModel(params, config) {
    console.log("page.js -> PageViewModel:", params, config);
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    self.pages = ko.observableArray([]);
    self.currentPage = ko.observable(new Page({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig?.fields) {
        self.searchConfig.fields.forEach(f => self.searchParams[f.name] = ko.observable(''));
    }

    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.maxPage = ko.computed(() => {
        const total = self.total() || 0;
        const size = self.size() || 1;
        return Math.max(1, Math.ceil(total / size));
    });
    self.sortField = ko.observable('id');
    self.sortOrder = ko.observable('ASC');

    self.resolveDbField = function(uiField) {
        const col = self.gridConfig?.columns?.find(c => c.name === uiField);
        return col?.dbField || uiField;
    };

    self.buildSearchQuery = function() {
        console.log("page.js -> buildSearchQuery: called");
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

    // ========== Use /search endpoint ==========
    self.loadPages = async function() {
        console.log("page.js -> loadPages: called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const url = `${API_PAGE}/search?${qs}`;
            console.log("page.js -> loadPages: url=", url);
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("page.js -> loadPages: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.pages(paged.items.map(p => new Page(p, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount) self.total(paged.totalCount);
            } else {
                self.pages([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load pages error:', err);
            self.pages([]);
            self.total(0);
        }
    };

    self.searchPages = function() {
        console.log("page.js -> searchPages: called");
        self.page(1);
        self.loadPages();
    };

    self.resetSearch = function() {
        console.log("page.js -> resetSearch: called");
        Object.keys(self.searchParams).forEach(k => this.searchParams[k](''));
        self.page(1);
        self.loadPages();
    };

    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.loadPages();
        }
    };

    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.loadPages();
        }
    };

    self.size.subscribe(() => {
        self.page(1);
        self.loadPages();
    });

    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadPages();
    };

    self.navigateToPages = function() {
        window.location.href = 'pages.jsp?reload=' + new Date().getTime();
    };

    self.addPage = function() {
        window.location.href = 'addPage.jsp';
    };

    self.editPage = function(page) {
        console.log("page.js -> editPage: page=", page);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editPageId', ko.unwrap(page.id));
        window.location.href = 'editPage.jsp';
    };

    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        console.log("page.js -> invokeAction: action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

    self.validateForm = function(pageObj, fieldsConfig) {
        console.log("page.js -> calling Validator.validateForm");
        return self.validator
            ? self.validator.validateForm(pageObj, fieldsConfig)
            : {};
    };

    self.savePage = async function() {
        console.log("page.js -> savePage: called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentPage(), self.formConfig.fields);
        console.log("page.js -> savePage: errs=", errs);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentPage()[f.name]());

        try {
            let url = `${API_PAGE}/create`;
            let method = 'POST';
            if (self.mode === 'edit' && self.currentPage().id && self.currentPage().id()) {
                const idVal = self.currentPage().id();
                url = `${API_PAGE}/update?id=${encodeURIComponent(idVal)}`;
                method = 'PUT';
            }
            console.log("page.js -> savePage: url=", url, "method=", method);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToPages();
        } catch (err) {
            console.error('Save page error:', err);
        }
    };

    self.deletePage = async function(page) {
        console.log("page.js -> deletePage: page=", page);
        if (!confirm('Are you sure?')) return;
        const idVal = ko.unwrap(page.id);
        try {
            const url = `${API_PAGE}/delete?id=${encodeURIComponent(idVal)}`;
            console.log("page.js -> deletePage: url=", url);
            await fetch(url, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadPages();
        } catch (err) {
            console.error('Delete page error:', err);
        }
    };

    self.loadPageById = async function(id) {
        console.log("page.js -> loadPageById: id=", id);
        try {
            const url = `${API_PAGE}/get?id=${encodeURIComponent(id)}`;
            console.log("page.js -> loadPageById: url=", url);
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("page.js -> loadPageById: data=", data);
            if (data.status === 'SUCCESS' && data.data) {
                self.currentPage(new Page(data.data, self.formConfig?.fields || []));
            }
        } catch (err) {
            console.error('Load page error:', err);
        }
    };

    console.log("page.js -> Initialization block: called");
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editPageId');
        console.log("page.js -> edit id=", id);
        if (id) self.loadPageById(id);
    } else if (self.mode === 'add') {
        console.log("page.js -> add mode");
        self.currentPage(new Page({}, self.formConfig?.fields || []));
    } else {
        console.log("page.js -> list mode");
        self.loadPages();
    }

    console.log("page.js -> Wrapper block: called");
    self.currentObject = self.currentPage;
    self.objects = self.pages;
}

export { Page, PageViewModel };
