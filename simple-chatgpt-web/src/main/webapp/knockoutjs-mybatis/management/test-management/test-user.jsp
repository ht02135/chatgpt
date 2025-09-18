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
// ✅ Correct API path
const API_BASE = '/chatgpt/api/management/users';

async function testUserAPI() {
    const tests = [
        { name: 'List Users', url: `${API_BASE}?page=0&size=5`, options: { method: 'GET' } },
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
        { name: 'Get User', url: `${API_BASE}/get?userName=john_doe`, options: { method: 'GET' } },
        { name: 'Update User', url: `${API_BASE}/update?userName=john_doe`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ firstName: "Johnny", email: "johnny@example.com" })
        }},
        { name: 'Delete User', url: `${API_BASE}/delete?userName=john_doe`, options: { method: 'DELETE' } }
    ];

    for (const t of tests) {
        try {
            const res = await fetch(t.url, t.options);
            if (!res.ok) {
                output.innerHTML += `Error calling ${t.name}: HTTP ${res.status}\n\n`;
                continue;
            }
            const json = await res.json();
            output.innerHTML += `#############\n`;
            output.innerHTML += `Test = ${t.name}\n`;
            output.innerHTML += `URL = ${t.url}\n`;
            output.innerHTML += `Options = ${JSON.stringify(t.options, null, 2)}\n`;
            output.innerHTML += `Response = ${JSON.stringify(json, null, 2)}\n`;
            output.innerHTML += `#############\n\n`;
        } catch (err) {
            output.innerHTML += `Error calling ${t.name}: ${err}\n\n`;
            console.error(`Error calling ${t.name}:`, err);
        }
    }
}

testUserAPI();
</script>
</body>
</html>
