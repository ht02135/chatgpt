<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test User API</title>
</head>
<body>
<h1>Test User API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_DATA = '/chatgpt/api/mybatis/data';
const TYPE_USER = 'user';

async function testUser() {
    const methods = [
        { name: 'Add', url: `${API_DATA}/add?type=${TYPE_USER}`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: "TestUser", email: "test@example.com" }) 
        }},
        { name: 'Update', url: `${API_DATA}/update?type=${TYPE_USER}`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: 1, name: "UpdatedUser", email: "updated@example.com" }) 
        }},
        { name: 'Update by ID', url: `${API_DATA}/1?type=${TYPE_USER}`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: 1, name: "UpdatedUser", email: "updated@example.com" }) 
        }},
        { name: 'Delete', url: `${API_DATA}/1?type=${TYPE_USER}`, options: { method: 'DELETE' }},
        { name: 'Get by ID', url: `${API_DATA}/1?type=${TYPE_USER}`, options: { method: 'GET' }},
        { name: 'Get by Key', url: `${API_DATA}/by-key/1?type=${TYPE_USER}`, options: { method: 'GET' }},
        { name: 'Get All', url: `${API_DATA}/all?type=${TYPE_USER}`, options: { method: 'GET' }},
        { name: 'Paged Simple', url: `${API_DATA}/paged-simple?type=${TYPE_USER}&page=1&size=5`, options: { method: 'GET' }}
    ];

    for (const m of methods) {
        try {
            const res = await fetch(m.url, m.options);
            const json = await res.json();
            console.log("#############");
            console.log(`Method = ${m.name}`);
            console.log("url = ", m.url);
            console.log("options = ", m.options);
            console.log("json = ", json);
            console.log("#############");

            output.innerHTML += `#############\n`;
            output.innerHTML += `Method = ${m.name}\n`;
            output.innerHTML += `url = ${m.url}\n`;
            output.innerHTML += `options = ${JSON.stringify(m.options, null, 2)}\n`;
            output.innerHTML += `json = ${JSON.stringify(json, null, 2)}\n`;
            output.innerHTML += `#############\n\n`;
        } catch (err) {
            console.error(`Error calling ${m.name}:`, err);
            output.innerHTML += `Error calling ${m.name}: ${err}\n`;
        }
    }
}

testUser();
</script>
</body>
</html>
