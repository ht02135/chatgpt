<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test UserManagement API</title>
<style>
    body { font-family: Arial; margin: 20px; }
    pre { background: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }
</style>
</head>
<body>
<h1>Test UserManagement API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_BASE = '/chatgpt/api/management/users';

async function testUserAPI() {
    const tests = [
        // 🔎 Search / List users
        { name: 'List Users (pagination)', url: `${API_BASE}?page=0&size=5`, options: { method: 'GET' } },
        // ➕ Create a user
        { name: 'Create User', url: `${API_BASE}/create`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userName: "john_doe",
                userKey: "key123",
                firstName: "John",
                lastName: "Doe",
                email: "john@example.com"
            })
        }},
        // 📖 Get by userName
        { name: 'Get User by userName', url: `${API_BASE}/get?userName=john_doe`, options: { method: 'GET' } },
        // ✏️ Update by userName
        { name: 'Update User by userName', url: `${API_BASE}/update?userName=john_doe`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ firstName: "Johnny", email: "johnny@example.com" })
        }},
        // 🗑 Delete by userName
        { name: 'Delete User by userName', url: `${API_BASE}/delete?userName=john_doe`, options: { method: 'DELETE' } }
    ];

    for (const t of tests) {
        console.group(`Preparing test: ${t.name}`);
        console.log("URL to call:", t.url);
        console.log("Options to use:", t.options);
        console.groupEnd();

        output.innerHTML += `\nPreparing test: ${t.name}\nURL: ${t.url}\nOptions: ${JSON.stringify(t.options, null, 2)}\n\n`;

        try {
            const res = await fetch(t.url, t.options);

            // Broken-down logging for response status
            console.log("Response status: res.status =", res.status);
            console.log("Response status: res.statusText =", res.statusText);

            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }

            const json = await res.json();

            // Detailed JSON logging
            console.log("Response JSON:", json);

            output.innerHTML += `Response:\n${JSON.stringify(json, null, 2)}\n`;
        } catch (err) {
            console.error(`Error calling ${t.name}:`, err);
            output.innerHTML += `Error calling ${t.name}: ${err}\n`;
        }

        console.log("---------- End of test ----------\n");
    }
}

testUserAPI();
</script>
</body>
</html>
