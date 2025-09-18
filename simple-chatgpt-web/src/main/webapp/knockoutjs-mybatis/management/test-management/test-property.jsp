<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test PropertyManagement API</title>
</head>
<body>
<h1>Test PropertyManagement API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
// Base API path from your PropertyManagementController
const API_BASE = '/chatgpt/api/management/config';

async function testPropertyAPI() {
    const tests = [
        // ➕ Create a couple of properties
        { name: 'Create Property 1', url: `${API_BASE}/create`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ propertyName: "prop1", propertyKey: "key1", type: "string", value: "value1" })
        }},
        { name: 'Create Property 2', url: `${API_BASE}/create`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ propertyName: "prop2", propertyKey: "key2", type: "boolean", value: "true" })
        }},

        // 📖 Get by ID (assuming ID = 1 exists)
        { name: 'Get by ID', url: `${API_BASE}/get?id=1`, options: { method: 'GET' }},
        // 📖 Get by propertyName
        { name: 'Get by propertyName', url: `${API_BASE}/get?propertyName=prop1`, options: { method: 'GET' }},
        // 📖 Get by propertyKey
        { name: 'Get by propertyKey', url: `${API_BASE}/get?propertyKey=key2`, options: { method: 'GET' }},

        // ✏️ Update by ID
        { name: 'Update by ID', url: `${API_BASE}/update?id=1`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ value: "newValue1" })
        }},
        // ✏️ Update by propertyName
        { name: 'Update by propertyName', url: `${API_BASE}/update?propertyName=prop2`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ value: "false" })
        }},

        // 🗑 Delete by propertyKey
        { name: 'Delete by propertyKey', url: `${API_BASE}/delete?propertyKey=key2`, options: { method: 'DELETE' }},

        // 🔎 Search with pagination
        { name: 'Search Simple Pagination', url: `${API_BASE}?page=0&size=20`, options: { method: 'GET' }},
        // 🔎 Search with filters
        { name: 'Search With Filters', url: `${API_BASE}?propertyName=prop1&type=string&page=0&size=10`, options: { method: 'GET' }},
        // 🔎 Search with sorting
        { name: 'Search With Sorting', url: `${API_BASE}?sortField=propertyName&sortDirection=desc`, options: { method: 'GET' }}
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

testPropertyAPI();
</script>
</body>
</html>
