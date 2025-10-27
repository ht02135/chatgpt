// property.js

// const API_PROPERTY = '/chatgpt/api/management/properties';
// detect context path from current URL
const PROPERTY_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
// build endpoint using detected context path
const API_PROPERTY = `${PROPERTY_CONTEXT_PATH}/api/management/properties`;

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
	
	// Pagination state
	//This is the current page number the user sees in the UI.
	self.page = ko.observable(1); 	
	//This is the page size = how many items are shown per page.	
	self.size = ko.observable(10);
	//This is the total number of items available in the dataset.
	self.total = ko.observable(0);
	//This is the total number of pages. Calculated as ceil(total / size).
	self.maxPage = ko.computed(() => {
	    const total = self.total() || 0;
	    const size = self.size() || 1;
	    return Math.max(1, Math.ceil(total / size));
	});
	self.sortField = ko.observable('id');
	self.sortOrder = ko.observable('ASC');

	// ========================
	// Helper: resolve sortField -> dbField
	// ========================
	self.resolveDbField = function(uiField) {
	    const col = self.gridConfig?.columns?.find(c => c.name === uiField);
	    return col?.dbField || uiField; // fallback to uiField if no mapping
	};
	
    // ========================
    // Build Query
    // ========================
    self.buildSearchQuery = function() {
        console.log("property.js -> buildSearchQuery: called");
        const params = new URLSearchParams();
        params.append('page', self.page() - 1); // backend expects 0-based
        params.append('size', self.size());
		params.append('sortField', self.resolveDbField(self.sortField()));
        params.append('sortDirection', self.sortOrder());

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = ko.unwrap(self.searchParams[f.name]);
                if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
            });
        }

        if (self.searchKey()) params.append('key', self.searchKey());
        if (self.searchType()) params.append('type', self.searchType());

        return params.toString();
    };

    // ========================
    // Load Properties
    // ========================
    self.loadProperties = async function() {
        console.log("property.js -> loadProperties: called");

        const qs = self.buildSearchQuery();
        const url = API_PROPERTY + "?" + qs;

        try {
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const resp = await res.json();
            if (resp && resp.data) {
                const arr = resp.data.items || [];
                self.properties(arr.map(p => new Property(p)));
				resp.data.totalCount && self.total() !== resp.data.totalCount ? self.total(resp.data.totalCount) : null;
            }
        } catch (err) {
            console.error('Load properties error:', err);
            self.properties([]);
            self.total(0);
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
    self.navigateToProperties = function() {
        console.log("property.js -> navigateToProperties: called");
        window.location.href = 'properties.jsp?reload=' + new Date().getTime();
    };
    self.addProperty = function() {
        console.log("property.js -> addProperty: called");
		/*
		we dont allow add property from UI, because is pointless.
		1>any system level property need to be added thru PropertyKey.
		2>of course, we can enable user to create new property via UI,
		  but it will be a pointless property that will not be used 
		  anywhere. that is why we disable add property from UI
		*/
        // window.location.href = 'addProperty.jsp';
    };
	self.editProperty = function(property) {
		console.log("property.js -> editProperty: #############");
		console.log("property.js -> editProperty: property=", property);
		console.log("property.js -> editProperty: ko.unwrap(property.id)=", ko.unwrap(property.id));
		console.log("property.js -> editProperty: #############");

		if (!confirm('Are you sure?')) return;
		
		localStorage.setItem('editPropertyId', ko.unwrap(property.id));
		console.log("property.js -> editProperty: ##########");
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
	        self[action.jsMethod](row);
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
	        let url = `${API_PROPERTY}/create`, method = 'POST';

	        // Use ID-based update, like saveUser
	        const idVal = self.currentProperty().id && self.currentProperty().id();
	        if (self.mode === 'edit' && idVal) {
	            url = `${API_PROPERTY}/update?id=${encodeURIComponent(idVal)}`;
	            method = 'PUT';
	        }
			
			console.log("property.js -> saveProperty: ##########");
			console.log("property.js -> saveProperty: self.mode=", self.mode);
			console.log("property.js -> saveProperty: idVal=", idVal);
			console.log("property.js -> saveProperty: url=", url);
			console.log("property.js -> saveProperty: method=", method);
			console.log("property.js -> saveProperty: payload=", payload);
			console.log("property.js -> saveProperty: ##########");
	        await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
	        self.navigateToProperties();
	    } catch (err) {
	        console.error('Save property error:', err);
	    }
	};

    // ========================
    // Load Property by ID
    // ========================
	self.loadPropertyById = async function(id) {
	    console.log("property.js -> loadPropertyById: id=", id);
	    if (!id) return;

	    try {
	        const res = await fetch(`${API_PROPERTY}/get?id=${id}`, {
	            headers: { 'Accept': 'application/json' }
	        });
	        const data = await res.json();
	        if (data.status === 'SUCCESS' && data.data) {
	            self.currentProperty(new Property(data.data, self.formConfig?.fields || []));
	        }
	    } catch (err) {
	        console.error('Load property error:', err);
	    }
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
	self.objects = self.properties;
}

export { Property, PropertyViewModel };