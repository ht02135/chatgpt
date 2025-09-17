<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test UserManagement API</title>
</head>
<body>
<h1>Test UserManagement API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
// Base API path from your controller
const API_BASE = '/management/users';

async function testUserAPI() {
    const tests = [
        // ➕ Create a couple of users
        { name: 'Create User 1', url: `${API_BASE}/create`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userName: "john_doe", userKey: "key123", firstName: "John", lastName: "Doe", email: "john@example.com" })
        }},
        { name: 'Create User 2', url: `${API_BASE}/create`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userName: "jane_smith", userKey: "key456", firstName: "Jane", lastName: "Smith", email: "jane@example.com" })
        }},

        // 📖 Get by ID (assuming ID = 1 exists)
        { name: 'Get by ID', url: `${API_BASE}/get?id=1`, options: { method: 'GET' }},
        // 📖 Get by userName
        { name: 'Get by userName', url: `${API_BASE}/get?userName=john_doe`, options: { method: 'GET' }},
        // 📖 Get by userKey
        { name: 'Get by userKey', url: `${API_BASE}/get?userKey=key456`, options: { method: 'GET' }},

        // ✏️ Update by ID
        { name: 'Update by ID', url: `${API_BASE}/update?id=1`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ firstName: "Johnny", email: "johnny@example.com" })
        }},
        // ✏️ Update by userName
        { name: 'Update by userName', url: `${API_BASE}/update?userName=jane_smith`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ lastName: "Smythe" })
        }},

        // 🗑 Delete by userKey
        { name: 'Delete by userKey', url: `${API_BASE}/delete?userKey=key456`, options: { method: 'DELETE' }},

        // 🔎 Search with pagination
        { name: 'Search Simple Pagination', url: `${API_BASE}?page=0&size=20`, options: { method: 'GET' }},
        // 🔎 Search with filters
        { name: 'Search With Filters', url: `${API_BASE}?firstName=John&city=New+York&page=1&size=10`, options: { method: 'GET' }},
        // 🔎 Search with sorting
        { name: 'Search With Sorting', url: `${API_BASE}?sortField=last_name&sortDirection=desc`, options: { method: 'GET' }}
    ];

    for (const t of tests) {
        try {
            const res = await fetch(t.url, t.options);
            const json = await res.json();
            console.log("#############");
            console.log(`Method = ${t.name}`);
            console.log("url = ", t.url);
            console.log("options = ", t.options);
            console.log("json = ", json);
            console.log("#############");

            output.innerHTML += `#############\n`;
            output.innerHTML += `Method = ${t.name}\n`;
            output.innerHTML += `url = ${t.url}\n`;
            output.innerHTML += `options = ${JSON.stringify(t.options, null, 2)}\n`;
            output.innerHTML += `json = ${JSON.stringify(json, null, 2)}\n`;
            output.innerHTML += `#############\n\n`;
        } catch (err) {
            console.error(`Error calling ${t.name}:`, err);
            output.innerHTML += `Error calling ${t.name}: ${err}\n`;
        }
    }
}

testUserAPI();
</script>
</body>
</html>
