// -------------------------
// user.js - frontend API client
// -------------------------

// Base URL for your API
// const API_USER = "/chatgpt/api/users";
// detect context path dynamically from URL
const USER_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
// build API endpoint using the detected context path
const API_USER = `${USER_CONTEXT_PATH}/api/users`;

// -------------------------
// Load all users
// -------------------------
async function loadUsers() {
  try {
    const response = await fetch(`${API_USER}/all`, {
      headers: { "Accept": "application/json" }
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const result = await response.json();
    console.log("Users loaded:", result.data);
    return result.data; // Array<User>
  } catch (err) {
    console.error("Load users error:", err);
    return [];
  }
}

// -------------------------
// Add a new user
// -------------------------
async function addUser(user) {
  try {
    const response = await fetch(`${API_USER}/add`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      body: JSON.stringify(user)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const result = await response.json();
    console.log("User added:", result.data);
    return result.data; // The saved User
  } catch (err) {
    console.error("Add user error:", err);
    return null;
  }
}

// -------------------------
// Update user by ID
// -------------------------
async function updateUser(id, user) {
  try {
    const response = await fetch(`${API_USER}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      body: JSON.stringify(user)
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const result = await response.json();
    console.log("User updated:", result.data);
    return result.data;
  } catch (err) {
    console.error("Update user error:", err);
    return null;
  }
}

// -------------------------
// Delete user by ID
// -------------------------
async function deleteUser(id) {
  try {
    const response = await fetch(`${API_USER}/${id}`, {
      method: "DELETE",
      headers: { "Accept": "application/json" }
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const result = await response.json();
    console.log("User deleted:", result.message);
    return true;
  } catch (err) {
    console.error("Delete user error:", err);
    return false;
  }
}

// -------------------------
// Get one user by ID
// -------------------------
async function getUser(id) {
  try {
    const response = await fetch(`${API_USER}/${id}`, {
      headers: { "Accept": "application/json" }
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const result = await response.json();
    console.log("User loaded:", result.data);
    return result.data;
  } catch (err) {
    console.error("Get user error:", err);
    return null;
  }
}

// -------------------------
// Test endpoint
// -------------------------
async function testApi() {
  try {
    const response = await fetch(`${API_USER}/test`, {
      headers: { "Accept": "application/json" }
    });
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

    const text = await response.text();
    console.log("API test:", text);
    return text;
  } catch (err) {
    console.error("Test API error:", err);
    return null;
  }
}

// -------------------------
// Expose functions globally (optional)
// -------------------------
window.UserApi = {
  loadUsers,
  addUser,
  updateUser,
  deleteUser,
  getUser,
  testApi
};
