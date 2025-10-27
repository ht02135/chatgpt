// jobRequest.js

function JobRequest(data, fields) {
    console.log("jobRequest.js -> JobRequest: called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function JobRequestViewModel(params, config) {
    console.log("jobRequest.js -> JobRequestViewModel:", params, config);
    const self = this;

    self.mode = params.mode || 'list';
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    self.jobRequests = ko.observableArray([]);
    self.currentJobRequest = ko.observable(new JobRequest({}, self.formConfig?.fields || []));
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
        console.log("jobRequest.js -> buildSearchQuery: called");
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
	
	self.runBatch = async function(job) {
	    console.log("jobRequest.js -> runBatch: job=", job);
	    if (!confirm('Are you sure you want to run the batch job?')) return;

	    try {
			const url = API_BATCH_REQUEST + "/runUserListJob";
	        console.log("Calling:", url);

	        const response = await fetch(url, {
	            method: "GET",
	            headers: { "Content-Type": "application/json" }
	        });

	        if (!response.ok) {
	            const text = await response.text();
	            console.error("runBatch failed:", text);
	            alert("Batch job failed: " + text);
	            return;
	        }

	        const result = await response.json();
	        console.log("runBatch result =", result);

	        if (result.success) {
	            alert("✅ Job started successfully!");
	        } else {
	            alert("⚠️ Job failed: " + (result.message || "Unknown error"));
	        }

	    } catch (e) {
	        console.error("runBatch exception:", e);
	        alert("Error: " + e.message);
	    }
	};
	
    // ========== Use /search endpoint ==========
    self.loadJobRequests = async function() {
        console.log("jobRequest.js -> loadJobRequests: called");
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
            const url = `${API_JOB_REQUEST}/search?${qs}`;
            console.log("jobRequest.js -> loadJobRequests: url=", url);
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("jobRequest.js -> loadJobRequests: response=", data);

            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
                self.jobRequests(paged.items.map(j => new JobRequest(j, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount) self.total(paged.totalCount);
            } else {
                self.jobRequests([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load JobRequests error:', err);
            self.jobRequests([]);
            self.total(0);
        }
    };

    self.searchJobRequests = function() {
        console.log("jobRequest.js -> searchJobRequests: called");
        self.page(1);
        self.loadJobRequests();
    };

    self.resetSearch = function() {
        console.log("jobRequest.js -> resetSearch: called");
        Object.keys(self.searchParams).forEach(k => this.searchParams[k](''));
        self.page(1);
        self.loadJobRequests();
    };

    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.loadJobRequests();
        }
    };

    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.loadJobRequests();
        }
    };

    self.size.subscribe(() => {
        self.page(1);
        self.loadJobRequests();
    });

    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadJobRequests();
    };

    self.navigateToJobRequests = function() {
        window.location.href = 'jobRequests.jsp?reload=' + new Date().getTime();
    };

    self.addJobRequest = function() {
        window.location.href = 'addJobRequest.jsp';
    };

	self.resetJobRequest = async function(job) {
	    console.log("jobRequest.js -> resetJobRequest: job=", job);
	    if (!confirm('Are you sure?')) return;

	    const idVal = ko.unwrap(job.id);
	    try {
	        const url = `${API_JOB_REQUEST}/reset?id=${encodeURIComponent(idVal)}`;
	        console.log("jobRequest.js -> resetJobRequest: url=", url);
	        await fetch(url, { method: 'POST', headers: { 'Accept': 'application/json' } });
	        self.loadJobRequests();
	    } catch (err) {
	        console.error('Reset JobRequest error:', err);
	    }
	};

	
    self.editJobRequest = function(job) {
        console.log("jobRequest.js -> editJobRequest: job=", job);
        if (!confirm('Are you sure?')) return;
        localStorage.setItem('editJobRequestId', ko.unwrap(job.id));
        window.location.href = 'editJobRequest.jsp';
    };

    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const actionGroup = self.actionGroupMap[column.actions];
        return Array.isArray(actionGroup) ? actionGroup.filter(a => a.visible !== false) : [];
    };

    self.invokeAction = function(action, row) {
        console.log("jobRequest.js -> invokeAction: action=", action, "row=", row);
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
            self[action.jsMethod](row);
        } else {
            console.warn("No JS method found for action:", action);
        }
    };

    self.validateForm = function(jobObj, fieldsConfig) {
        console.log("jobRequest.js -> calling Validator.validateForm");
        return self.validator
            ? self.validator.validateForm(jobObj, fieldsConfig)
            : {};
    };

    self.saveJobRequest = async function() {
        console.log("jobRequest.js -> saveJobRequest: called");
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentJobRequest(), self.formConfig.fields);
        console.log("jobRequest.js -> saveJobRequest: errs=", errs);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        const payload = {};
        self.formConfig.fields.forEach(f => payload[f.name] = self.currentJobRequest()[f.name]());

        try {
            let url = `${API_JOB_REQUEST}/create`;
            let method = 'POST';
            if (self.mode === 'edit' && self.currentJobRequest().id && self.currentJobRequest().id()) {
                const idVal = self.currentJobRequest().id();
                url = `${API_JOB_REQUEST}/update?id=${encodeURIComponent(idVal)}`;
                method = 'PUT';
            }
            console.log("jobRequest.js -> saveJobRequest: self.mode=", self.mode);
            console.log("jobRequest.js -> saveJobRequest: self.currentJobRequest()=", self.currentJobRequest());
            console.log("jobRequest.js -> saveJobRequest: url=", url);
            console.log("jobRequest.js -> saveJobRequest: method=", method);
            console.log("jobRequest.js -> saveJobRequest: payload=", payload);
            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToJobRequests();
        } catch (err) {
            console.error('Save JobRequest error:', err);
        }
    };

    self.deleteJobRequest = async function(job) {
        console.log("jobRequest.js -> deleteJobRequest: job=", job);
        if (!confirm('Are you sure?')) return;
        const idVal = ko.unwrap(job.id);
        try {
            const url = `${API_JOB_REQUEST}/delete?id=${encodeURIComponent(idVal)}`;
            console.log("jobRequest.js -> deleteJobRequest: url=", url);
            await fetch(url, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadJobRequests();
        } catch (err) {
            console.error('Delete JobRequest error:', err);
        }
    };

    self.loadJobRequestById = async function(id) {
        console.log("jobRequest.js -> loadJobRequestById: id=", id);
        try {
            const url = `${API_JOB_REQUEST}/get?id=${encodeURIComponent(id)}`;
            console.log("jobRequest.js -> loadJobRequestById: url=", url);
            const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
            const data = await res.json();
            console.log("jobRequest.js -> loadJobRequestById: data=", data);
            if (data.status === 'SUCCESS' && data.data) {
                self.currentJobRequest(new JobRequest(data.data, self.formConfig?.fields || []));
            }
        } catch (err) {
            console.error('Load JobRequest error:', err);
        }
    };

    console.log("jobRequest.js -> Initialization block: called");
    if (self.mode === 'edit') {
        const id = localStorage.getItem('editJobRequestId');
        console.log("jobRequest.js -> edit id=", id);
        if (id) self.loadJobRequestById(id);
    } else if (self.mode === 'add') {
        console.log("jobRequest.js -> add mode");
        self.currentJobRequest(new JobRequest({}, self.formConfig?.fields || []));
    } else {
        console.log("jobRequest.js -> list mode");
        self.loadJobRequests();
    }

    console.log("jobRequest.js -> Wrapper block: called");
    self.currentObject = self.currentJobRequest;
    self.objects = self.jobRequests;
}

export { JobRequest, JobRequestViewModel };
