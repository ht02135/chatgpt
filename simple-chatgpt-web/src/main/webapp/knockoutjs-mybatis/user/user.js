function User(data) {
    this.id = data.id || 0;
    this.name = ko.observable(data.name || "");
    this.email = ko.observable(data.email || "");
}

function UserViewModel() {
    var self = this;

    self.users = ko.observableArray([]);
    self.currentUser = ko.observable(new User({}));
    self.isEditing = ko.observable(false);

    // Paging and sorting observables
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.sortField = ko.observable("id");
    self.sortOrder = ko.observable("ASC");

    const API_BASE = "/chatgpt/api/mybatis/users";

    // Load users with paging and sorting
    self.loadUsers = async function() {
        try {
            const res = await fetch(`${API_BASE}/paged?page=${self.page()}&size=${self.size()}&sortField=${self.sortField()}&sortOrder=${self.sortOrder()}`,
                { headers: { "Accept": "application/json" } });
            const data = await res.json();
            if (data.status === "SUCCESS") {
                self.users((data.data.users || []).map(u => new User(u)));
                self.total(data.data.total || 0);
            } else {
                self.users([]);
                self.total(0);
            }
        } catch (err) {
            console.error("Load users error:", err);
        }
    };

    // Save or update user
    self.saveUser = async function() {
        const u = self.currentUser();
        const payload = { name: u.name(), email: u.email() };
        try {
            let url = `${API_BASE}/add`;
            let method = "POST";

            if (self.isEditing()) {
                url = `${API_BASE}/${u.id}`;
                method = "PUT";
                payload.id = u.id;
            }

            await fetch(url, {
                method,
                headers: { "Content-Type": "application/json", "Accept": "application/json" },
                body: JSON.stringify(payload)
            });

            self.cancelEdit();
            self.loadUsers();
        } catch (err) {
            console.error("Save user error:", err);
        }
    };

    self.editUser = function(user) {
        self.currentUser(new User({ id: user.id, name: user.name(), email: user.email() }));
        self.isEditing(true);
    };

    self.deleteUser = async function(user) {
        if (!confirm("Are you sure you want to delete this user?")) return;
        try {
            await fetch(`${API_BASE}/${user.id}`, { method: "DELETE", headers: { "Accept": "application/json" } });
            self.loadUsers();
        } catch (err) {
            console.error("Delete user error:", err);
        }
    };

    self.cancelEdit = function() {
        self.currentUser(new User({}));
        self.isEditing(false);
    };

    // Pagination controls
    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.loadUsers();
        }
    };
    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.loadUsers();
        }
    };
    self.maxPage = ko.computed(function() {
        return Math.ceil(self.total() / self.size());
    });

    // Sorting
    self.setSort = function(field) {
        if (self.sortField() === field) {
            self.sortOrder(self.sortOrder() === "ASC" ? "DESC" : "ASC");
        } else {
            self.sortField(field);
            self.sortOrder("ASC");
        }
        self.page(1);
        self.loadUsers();
    };

    // Initial load
    self.loadUsers();

    // Subscribe to page/size changes
    self.page.subscribe(self.loadUsers);
    self.size.subscribe(self.loadUsers);
}

// Activate Knockout bindings
ko.applyBindings(new UserViewModel());