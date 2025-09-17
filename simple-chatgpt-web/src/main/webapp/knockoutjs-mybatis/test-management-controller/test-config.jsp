<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test Management Config API</title>
</head>
<body>
<h1>Test Management Config API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_CONFIG = '/chatgpt/api/management/config';

async function testConfig() {
    const methods = [
        { name: 'Get Config', url: API_CONFIG, options: { method: 'GET' } }
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
