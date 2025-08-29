// user.js - Complete User Management with Fetch API
document.addEventListener("DOMContentLoaded", function () {
    let editingUserId = null;
    const API_BASE = "http://localhost:8080/chatgpt/api/users";

    // Get form and elements
    const form = document.getElementById("addUserForm");
    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const submitBtn = document.getElementById("submitBtn");
    const cancelBtn = document.getElementById("cancelBtn");
    const userTableBody = document.querySelector("#userList tbody");

    if (!form || !nameInput || !emailInput) {
        console.error("Required form elements not found!");
        return;
    }

    // Add/Update User Form Submit
    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const name = nameInput.value.trim();
        const email = emailInput.value.trim();

        if (!name || !email) {
            alert("Please fill in both name and email fields.");
            return;
        }

        const user = { name, email };

        // If editing, add the ID
        if (editingUserId) {
            user.id = editingUserId;
        }

        const url = editingUserId ? `${API_BASE}/${editingUserId}` : API_BASE;
        const method = editingUserId ? "PUT" : "POST";

        console.log(`${method} request to:`, url, "Data:", user);

        fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        })
        .then(response => {
            console.log("Response status:", response.status);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Success response:", data);
            const action = editingUserId ? 'updated' : 'added';
            alert(`User ${action} successfully!`);
            resetForm();
            loadUsers();
        })
        .catch(error => {
            console.error("Save error:", error);
            alert("Failed to save user. Check console for details.");
        });
    });

    // Cancel Edit
    cancelBtn.addEventListener("click", function() {
        resetForm();
    });

    // Event delegation for Edit and Delete buttons
    userTableBody.addEventListener("click", function(e) {
        if (e.target.classList.contains("editUser")) {
            const userId = e.target.dataset.id;
            editUser(userId);
        } else if (e.target.classList.contains("deleteUser")) {
            const userId = e.target.dataset.id;
            deleteUser(userId);
        }
    });

    // Edit User
    function editUser(id) {
        fetch(`${API_BASE}/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === "SUCCESS") {
                    const user = data.data;
                    nameInput.value = user.name;
                    emailInput.value = user.email;
                    editingUserId = user.id;
                    submitBtn.textContent = "Update User";
                    cancelBtn.style.display = "inline-block";
                } else {
                    alert('Error loading user: ' + data.message);
                }
            })
            .catch(error => {
                console.error("Load user error:", error);
                alert('Failed to load user. Check console for details.');
            });
    }

    // Delete User
    function deleteUser(id) {
        if (confirm('Are you sure you want to delete this user?')) {
            fetch(`${API_BASE}/${id}`, {
                method: 'DELETE'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === "SUCCESS") {
                    alert('User deleted successfully');
                    loadUsers();
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error("Delete error:", error);
                alert('Network error. Check console for details.');
            });
        }
    }

    // Reset Form
    function resetForm() {
        form.reset();
        editingUserId = null;
        submitBtn.textContent = "Add User";
        cancelBtn.style.display = "none";
    }

    // Load Users
    function loadUsers() {
        fetch(API_BASE)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === "SUCCESS") {
                    displayUsers(data.data);
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error("Load users error:", error);
                alert('Failed to load users. Check console for details.');
            });
    }

    // Display Users in Table
    function displayUsers(users) {
        userTableBody.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>
                    <button class="editUser" data-id="${user.id}">Edit</button>
                    <button class="deleteUser" data-id="${user.id}">Delete</button>
                </td>
            `;
            userTableBody.appendChild(row);
        });
    }

    // Load users on page load
    loadUsers();
});