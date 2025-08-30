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

    const API_BASE = "/chatgpt/api/users";

    // Load users from backend
    self.loadUsers = async function() {
        try {
            const res = await fetch(`${API_BASE}/all`, { headers: { "Accept": "application/json" } });
            const data = await res.json();
            if (data.status === "SUCCESS") {
                self.users(data.data.map(u => new User(u)));
            } else {
                self.users([]);
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

    // Initial load
    self.loadUsers();
}

// Activate Knockout bindings
ko.applyBindings(new UserViewModel());
