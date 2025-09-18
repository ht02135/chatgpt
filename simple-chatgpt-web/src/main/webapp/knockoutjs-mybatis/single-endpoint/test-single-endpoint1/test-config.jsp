<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test Config API</title>
</head>
<body>
<h1>Test Config API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_DATA = '/chatgpt/api/mybatis/data';
const TYPE_CONFIG = 'config';

async function testConfig() {
    const methods = [
        { name: 'Add', url: `${API_DATA}/add?type=${TYPE_CONFIG}`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({}) // empty payload for test
        }},
        { name: 'Update', url: `${API_DATA}/update?type=${TYPE_CONFIG}`, options: {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({}) // empty payload
        }},
        { name: 'Update by ID', url: `${API_DATA}/1?type=${TYPE_CONFIG}`, options: {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({}) // empty payload
        }},
        { name: 'Delete', url: `${API_DATA}/1?type=${TYPE_CONFIG}`, options: { method: 'DELETE' }},
        { name: 'Get by ID', url: `${API_DATA}/1?type=${TYPE_CONFIG}`, options: { method: 'GET' }},
        { name: 'Get by Key', url: `${API_DATA}/by-key/someKey?type=${TYPE_CONFIG}`, options: { method: 'GET' }},
        { name: 'Get All', url: `${API_DATA}/all?type=${TYPE_CONFIG}`, options: { method: 'GET' }},
        { name: 'Paged Simple', url: `${API_DATA}/paged-simple?type=${TYPE_CONFIG}&page=1&size=5`, options: { method: 'GET' }}
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

testConfig();
</script>
</body>
</html>
