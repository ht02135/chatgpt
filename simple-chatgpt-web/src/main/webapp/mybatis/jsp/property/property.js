// property.js

// const API_PROPERTY = '/chatgpt/api/mybatis/properties';
// Get context path dynamically from the URL
const PROPERTY_CONTEXT_PATH = window.location.pathname.split('/')[1];
// Construct the API endpoints
const API_PROPERTY = `/${PROPERTY_CONTEXT_PATH}/api/mybatis/properties`;

// --- Model ---
function Property(data, fields) {
    console.log("property.js -> Property: called");
    const self = this;
    if (fields && fields.length) {
        fields.forEach(f => {
            self[f.name] = ko.observable(data && data[f.name] || '');
        });
    } else {
        // fallback: copy all properties
        if (data) Object.keys(data).forEach(k => self[k] = ko.observable(data[k]));
    }
}

// --- ViewModel ---
function PropertyViewModel(params, config) {
    console.log("property.js -> PropertyViewModel:", params, config);
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    self.properties = ko.observableArray([]);
    self.currentProperty = ko.observable(new Property({}, self.formConfig?.fields || []));
    self.errors = ko.observable({});

    self.searchParams = {};
    if (self.searchConfig && self.searchConfig.fields) {
        self.searchConfig.fields.forEach(f => {
            self.searchParams[f.name] = ko.observable('');
        });
    }

    self.searchKey = ko.observable('');
    self.searchType = ko.observable('');

    // Pagination
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable('key');
    self.sortOrder = ko.observable('ASC');
    //self.maxPage = ko.computed(() => Math.ceil(self.total() / self.size()));
	self.maxPage = ko.observable(1);

    // ========================
    // Build Query
    // ========================
    self.buildSearchQuery = function() {
        console.log("property.js -> buildSearchQuery: called");
        const params = new URLSearchParams();
        params.append('page', self.page());
        params.append('size', self.size());
        params.append('sortField', self.sortField());
        params.append('sortOrder', self.sortOrder());

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = ko.unwrap(self.searchParams[f.name]);
                if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
            });
        }

        return params.toString();
    };

    // ========================
    // Load Properties (Old Simplified Style)
    // ========================
    self.loadProperties = async function() {
        console.log("property.js -> loadProperties: called");

        const params = {
            key: self.searchKey(),
            type: self.searchType(),
            page: self.page(),
            size: self.size(),
            sort: self.sortField(),
            order: self.sortOrder()
        };

        const qs = new URLSearchParams(params).toString();
        const url = API_PROPERTY + "/all?" + qs;

        try {
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const resp = await res.json();
            if (resp && resp.data) {
                const arr = resp.data.properties || [];
                self.properties(arr.map(p => new Property(p)));
                self.total(resp.data.total || arr.length);
                self.maxPage(resp.data.maxPage || 1);
            }
        } catch (err) {
            console.error('Load properties error:', err);
            self.properties([]);
            self.total(0);
            self.maxPage(1);
        }
    };

    // ========================
    // Search & Reset
    // ========================
    self.searchProperties = function() {
        console.log("property.js -> searchProperties: called");
        self.page(1);
        self.loadProperties();
    };
    self.resetSearch = function() {
        console.log("property.js -> resetSearch: called");
        Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadProperties();
    };

    // ========================
    // Pagination
    // ========================
    self.nextPage = function() {
        console.log("property.js -> nextPage: called");
        if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadProperties(); }
    };
    self.prevPage = function() {
        console.log("property.js -> prevPage: called");
        if (self.page() > 1) { self.page(self.page() - 1); self.loadProperties(); }
    };
    self.size.subscribe(() => {
        console.log("size.subscribe: called");
        self.page(1); self.loadProperties();
    });

    // ========================
    // Sorting
    // ========================
    self.setSort = function(field) {
        console.log("property.js -> setSort: field=", field);
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadProperties();
    };

    // ========================
    // Navigation
    // ========================
    self.goProperties = function() {
        console.log("property.js -> goProperties: called");
        window.location.href = 'properties.jsp?reload=' + new Date().getTime();
    };
    self.goAddProperty = function() {
        console.log("property.js -> goAddProperty: called");
        window.location.href = 'addProperty.jsp';
    };
    self.goEditProperty = function(id) {
        console.log("property.js -> goEditProperty: id=", ko.unwrap(id));
        localStorage.setItem('editPropertyId', ko.unwrap(id));
        window.location.href = 'editProperty.jsp';
    };

    // ========================
    // Actions Resolver
    // ========================
    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        console.log("property.js -> invokeAction called", action, row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            if (action.jsMethod === "goEditProperty") {
                self[action.jsMethod](ko.unwrap(row.key));
            } else {
                self[action.jsMethod](row);
            }
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

	// ========================
	// Validation Helpers
	// ========================
	self.validateForm = function (propObj, fieldsConfig) {
	    console.log("property.js -> calling Validator.validateForm");
	    return self.validator
	        ? self.validator.validateForm(propObj, fieldsConfig)
	        : {};
	};

    // ========================
    // Save Property
    // ========================
    self.saveProperty = async function() {
        console.log("property.js -> saveProperty: called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentProperty(), self.formConfig.fields);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentProperty()[f.name]());

        try {
            let url = `${API_PROPERTY}/add`, method = 'POST';
            if (self.mode === 'edit' && self.currentProperty().key && self.currentProperty().key()) {
                url = `${API_PROPERTY}/${self.currentProperty().key()}`;
                method = 'PUT';
            }
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.goProperties();
        } catch (err) { console.error('Save property error:', err); }
    };

    // ========================
    // Load Property by ID
    // ========================
    self.loadPropertyById = async function(id) {
        console.log("property.js -> loadPropertyById: id=", id);
        try {
            const res = await fetch(`${API_PROPERTY}/${id}`, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            if (data.status === 'SUCCESS' && data.data) self.currentProperty(new Property(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load property error:', err); }
    };

    // ========================
    // Initialization
    // ========================
    console.log("Initialization block: called");
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editPropertyId');
        if (id) self.loadPropertyById(id);
    } else if (self.mode === 'add') {
        self.currentProperty(new Property({}, self.formConfig?.fields || []));
    } else {
        self.loadProperties();
    }

    // ========================
    // WRAPPER: MUST BE AT BOTTOM
    // ========================
    console.log("property.js -> Wrapper block: called");
    self.currentObject = self.currentProperty;
    self.formTitle = self.mode === 'edit' ? 'Edit Property' : 'Add Property';

    self.goBack = function() {
        console.log("property.js Wrapper -> goBack called");
        return self.goProperties();
    };
    self.saveObject = function() {
        console.log("property.js Wrapper -> saveObject called");
        return self.saveProperty();
    };
    self.goAddObject = function() {
        console.log("property.js Wrapper -> goAddObject called");
        return self.goAddProperty();
    };
    self.objects = self.properties;
    self.searchObjects = function() {
        console.log("property.js Wrapper -> searchObjects called");
        return self.searchProperties();
    };
}
