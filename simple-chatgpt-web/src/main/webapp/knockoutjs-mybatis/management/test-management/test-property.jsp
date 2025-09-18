<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test PropertyManagement API</title>
<style>
body { font-family: Arial; margin: 20px; }
pre { background: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }
</style>
</head>
<body>
<h1>Test PropertyManagement API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_BASE = '/chatgpt/api/management/properties';

async function testPropertyAPI() {

    // 1️⃣ Create Property 1
    try {
        console.log("Test 1: Create Property 1");
        output.innerHTML += "Test 1: Create Property 1\n";
        const res1 = await fetch(API_BASE + "/create", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ propertyName: "prop1", propertyKey: "key1", type: "string", value: "value1" })
        });
        console.log("res1.status =", res1.status);
        const json1 = await res1.json();
        console.log("JSON:", json1);
        output.innerHTML += JSON.stringify(json1, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 1:", err);
        output.innerHTML += "Error in Test 1: " + err + "\n\n";
    }

    // 2️⃣ Create Property 2
    try {
        console.log("Test 2: Create Property 2");
        output.innerHTML += "Test 2: Create Property 2\n";
        const res2 = await fetch(API_BASE + "/create", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ propertyName: "prop2", propertyKey: "key2", type: "boolean", value: "true" })
        });
        console.log("res2.status =", res2.status);
        const json2 = await res2.json();
        console.log("JSON:", json2);
        output.innerHTML += JSON.stringify(json2, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 2:", err);
        output.innerHTML += "Error in Test 2: " + err + "\n\n";
    }

    // 3️⃣ Get Property by ID
    try {
        console.log("Test 3: Get Property by ID");
        output.innerHTML += "Test 3: Get Property by ID\n";
        const res3 = await fetch(API_BASE + "/get?id=1", { method: 'GET' });
        console.log("res3.status =", res3.status);
        const json3 = await res3.json();
        console.log("JSON:", json3);
        output.innerHTML += JSON.stringify(json3, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 3:", err);
        output.innerHTML += "Error in Test 3: " + err + "\n\n";
    }

    // 4️⃣ Get Property by propertyName
    try {
        console.log("Test 4: Get Property by propertyName");
        output.innerHTML += "Test 4: Get Property by propertyName\n";
        const res4 = await fetch(API_BASE + "/get?propertyName=prop1", { method: 'GET' });
        console.log("res4.status =", res4.status);
        const json4 = await res4.json();
        console.log("JSON:", json4);
        output.innerHTML += JSON.stringify(json4, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 4:", err);
        output.innerHTML += "Error in Test 4: " + err + "\n\n";
    }

    // 5️⃣ Update Property by ID
    try {
        console.log("Test 5: Update Property by ID");
        output.innerHTML += "Test 5: Update Property by ID\n";
        const res5 = await fetch(API_BASE + "/update?id=1", {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ value: "newValue1" })
        });
        console.log("res5.status =", res5.status);
        const json5 = await res5.json();
        console.log("JSON:", json5);
        output.innerHTML += JSON.stringify(json5, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 5:", err);
        output.innerHTML += "Error in Test 5: " + err + "\n\n";
    }

    // 6️⃣ Delete Property by propertyKey
    try {
        console.log("Test 6: Delete Property by propertyKey");
        output.innerHTML += "Test 6: Delete Property by propertyKey\n";
        const res6 = await fetch(API_BASE + "/delete?propertyKey=key2", { method: 'DELETE' });
        console.log("res6.status =", res6.status);
        const json6 = await res6.json();
        console.log("JSON:", json6);
        output.innerHTML += JSON.stringify(json6, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 6:", err);
        output.innerHTML += "Error in Test 6: " + err + "\n\n";
    }

    // 7️⃣ Search Properties Simple Pagination
    try {
        console.log("Test 7: Search Properties Pagination");
        output.innerHTML += "Test 7: Search Properties Pagination\n";
        const res7 = await fetch(API_BASE + "?page=0&size=20", { method: 'GET' });
        console.log("res7.status =", res7.status);
        const json7 = await res7.json();
        console.log("JSON:", json7);
        output.innerHTML += JSON.stringify(json7, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 7:", err);
        output.innerHTML += "Error in Test 7: " + err + "\n\n";
    }

    console.log("---------- End of tests ----------");
    output.innerHTML += "---------- End of tests ----------\n";
}

testPropertyAPI();
</script>
</body>
</html>
