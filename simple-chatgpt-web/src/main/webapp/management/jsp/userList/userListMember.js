// userListMember.js

// const API_USERLIST_MEMBER = '/chatgpt/api/management/userlistmembers';
// detect context path dynamically
const USERTLISTMEMBER_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
// now build the endpoint correctly
const API_USERLIST_MEMBER = `${USERTLISTMEMBER_CONTEXT_PATH}/api/management/userlistmembers`;

function UserListMember(data, fields) {
    console.log("userListMember.js -> UserListMember: constructor called");
    const self = this;
    fields.forEach(f => {
        self[f.name] = ko.observable(data && data[f.name] || '');
    });
}

function UserListMemberViewModel(params, config) {
	console.log("userListMember.js -> UserListMemberViewModel: constructor called");
    const self = this;

    // ========================
    // Mode & Configs
    // ========================
	/*
	Hung : mode is treated as object
	self.mode = mode || 'list';
	*/
	/*
	Hung: params is the object you pass ({ mode: "list" }).
	      params.mode correctly accesses the string "list".
	*/
	self.mode = params.mode || 'list';
	self.listId = params.listId || localStorage.getItem('editUserListId');
    self.gridConfig = config?.grid;
    self.formConfig = config?.form;
    self.searchConfig = config?.search;
    self.actionGroupMap = config?.actionGroups || {};

    // ========================
    // Observables
    // ========================
    self.members = ko.observableArray([]);
    self.currentMember = ko.observable(new UserListMember({}, self.formConfig?.fields || []));
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

    self.buildSearchQuery = function() {
        const params = new URLSearchParams();
        params.append('page', self.page() - 1);
        params.append('size', self.size());
        params.append('sortField', self.resolveDbField(self.sortField()));
        params.append('sortDirection', self.sortOrder());
        params.append('listId', self.listId);

        if (self.searchConfig?.fields) {
            self.searchConfig.fields.forEach(f => {
                const val = self.searchParams[f.name]();
                if (val && val.toString().trim()) params.append(f.name, val.toString().trim());
            });
        }
        return params.toString();
    };

    // ========================
    // Load Members
    // ========================
    self.loadUserListMembers = async function() {
        console.log("userListMember.js -> loadUserListMembers called");
		console.log("userListMember.js -> loadUserListMembers: self.mode=",self.mode);
        if (self.mode !== 'list') return;

        try {
            const qs = self.buildSearchQuery();
			console.log("userListMember.js -> loadUserListMembers: qs=",qs);
            const res = await fetch(`${API_USERLIST_MEMBER}/search?${qs}`, { headers: { 'Accept': 'application/json' } });
			console.log("userListMember.js -> loadUserListMembers: #############");
			console.log("userListMember.js -> loadUserListMembers: res=",res);
			console.log("userListMember.js -> loadUserListMembers: #############");
			const data = await res.json();
			console.log("userListMember.js -> loadUserListMembers: data=",data);
			
            if (data.status === 'SUCCESS' && data.data) {
                const paged = data.data;
				console.log("userListMember.js -> loadUserListMembers: #############");
				console.log("userListMember.js -> loadUserListMembers: paged=",paged);
				console.log("userListMember.js -> loadUserListMembers: #############");
                self.members(paged.items.map(m => new UserListMember(m, self.gridConfig?.columns.map(c => ({ name: c.name })) || [])));
                if (paged.totalCount && self.total() !== paged.totalCount) self.total(paged.totalCount);
            } else {
                self.members([]);
                self.total(0);
            }
        } catch (err) {
            console.error('Load members error:', err);
            self.members([]);
            self.total(0);
        }
    };

    // Search & Reset
    self.searchUserListMembers = function() {
        console.log("userListMember.js -> searchUserListMembers called");
        self.page(1);
        self.loadUserListMembers();
    };
    self.resetSearch = function() {
        console.log("userListMember.js -> resetSearch called");
        self.searchParams && Object.keys(self.searchParams).forEach(k => self.searchParams[k](''));
        self.page(1);
        self.loadUserListMembers();
    };

    // Pagination
    self.nextPage = function() {
        if (self.page() < self.maxPage()) { self.page(self.page() + 1); self.loadUserListMembers(); }
    };
    self.prevPage = function() {
        if (self.page() > 1) { self.page(self.page() - 1); self.loadUserListMembers(); }
    };
    self.size.subscribe(() => { self.page(1); self.loadUserListMembers(); });

    // Sorting
    self.setSort = function(field) {
        if (self.sortField() === field) self.sortOrder(self.sortOrder() === 'ASC' ? 'DESC' : 'ASC');
        else { self.sortField(field); self.sortOrder('ASC'); }
        self.page(1);
        self.loadUserListMembers();
    };

    // Navigation
    self.navigateToMembers = function() { window.location.href = 'editUserList.jsp'; };
    self.addUserListMember = function() { window.location.href = 'addUserListMember.jsp'; };

    self.editUserListMember = function(member) {
		console.log("userListMember.js -> edieditUserListMembertUserList: #############");
		console.log("userListMember.js -> edieditUserListMembertUserList: member=", member);
		console.log("userListMember.js -> edieditUserListMembertUserList: ko.unwrap(member.id)=", ko.unwrap(member.id));
		console.log("userListMember.js -> edieditUserListMembertUserList: #############");

		if (!confirm('Are you sure?')) return;
        localStorage.setItem('editUserListMemberId', ko.unwrap(member.id));
        window.location.href = 'editUserListMember.jsp';
    };

    // Action Resolver
    self.getActionsForColumn = function(column) {
        if (!column.actions) return [];
        const group = self.actionGroupMap[column.actions];
        return Array.isArray(group) ? group.filter(a => a.visible !== false) : [];
    };
    self.invokeAction = function(action, row) {
        if (action && action.jsMethod && typeof self[action.jsMethod] === 'function') {
			console.log("userListMember.js -> invokeAction: action=", action);
			console.log("userListMember.js -> invokeAction: action.jsMethod=", action.jsMethod);
			self[action.jsMethod](row);
        } else console.warn("No JS method found for action:", action);
    };

    // Validation & Save
    self.validateForm = function(obj, fields) {
        return self.validator ? self.validator.validateForm(obj, fields) : {};
    };

    self.saveUserListMember = async function() {
        console.log("userListMember.js -> saveUserListMember called");
		console.log("userListMember.js -> saveUserListMember self.formConfig=",self.formConfig);
        if (!self.formConfig) return;

        self.errors({});
        const errs = self.validateForm(self.currentMember(), self.formConfig.fields);
		console.log("userListMember.js -> saveUserListMember errs=",errs);
        if (Object.keys(errs).length > 0) {
            self.errors(errs);
            return;
        }

        try {
			const payload = {
			    ...ko.toJS(self.currentMember()),
			    listId: self.listId
			};

            let url = `${API_USERLIST_MEMBER}/create`, method = 'POST';
            if (self.mode === 'edit' && self.currentMember().id && self.currentMember().id()) {
                url = `${API_USERLIST_MEMBER}/update?memberId=${encodeURIComponent(self.currentMember().id())}`;
                method = 'PUT';
            }
			console.log("userListMember.js -> saveUserListMember url=",url);
			console.log("userListMember.js -> saveUserListMember method=",method);
			console.log("userListMember.js -> saveUserListMember payload=",payload);

            await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
            self.navigateToMembers();
        } catch (err) { console.error('Save member error:', err); }
    };

    // Delete
    self.deleteUserListMember = async function(row) {
        if (!confirm('Are you sure?')) return;
        try {
            await fetch(`${API_USERLIST_MEMBER}/delete?memberId=${encodeURIComponent(ko.unwrap(row.id))}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
            self.loadUserListMembers();
        } catch (err) { console.error('Delete member error:', err); }
    };

    // Load by ID
    self.loadUserListMemberById = async function(id) {
		console.log("userListMember.js -> loadUserListMemberById called");
		console.log("userListMember.js -> loadUserListMemberById id=",id);
        try {
            const res = await fetch(`${API_USERLIST_MEMBER}/get?memberId=${encodeURIComponent(id)}`, { headers: { 'Accept': 'application/json' } });
			console.log("userListMember.js -> loadUserListMemberById res=",res);
			const data = await res.json();
			console.log("userListMember.js -> loadUserListMemberById data=",data);
			
            if (data.status === 'SUCCESS' && data.data) self.currentMember(new UserListMember(data.data, self.formConfig?.fields || []));
        } catch (err) { console.error('Load member error:', err); }
    };

    // Initialization
    if (self.mode === 'edit') {
        const memberId = localStorage.getItem('editUserListMemberId');
        if (memberId) self.loadUserListMemberById(memberId);
    } else if (self.mode === 'add') {
        self.currentMember(new UserListMember({}, self.formConfig?.fields || []));
    } else {
        self.loadUserListMembers();
    }
    
    // Wrapper
    self.currentObject = self.currentMember;
    self.objects = self.members;
}

// export them both together at the bottom
export { UserListMember, UserListMemberViewModel };