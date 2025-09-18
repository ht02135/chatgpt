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
    // 1️⃣ List users
    try {
        console.log("Test 1: List Users");
        output.innerHTML += "Test 1: List Users\n";
        const res1 = await fetch(API_BASE + "?page=0&size=5", { method: 'GET' });
        console.log("Response status: res1.status =", res1.status);
        console.log("Response status: res1.statusText =", res1.statusText);
        const json1 = await res1.json();
        console.log("Response JSON:", json1);
        output.innerHTML += JSON.stringify(json1, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 1:", err);
        output.innerHTML += "Error in Test 1: " + err + "\n\n";
    }

    // 2️⃣ Create User
    try {
        console.log("Test 2: Create User");
        output.innerHTML += "Test 2: Create User\n";
        const res2 = await fetch(API_BASE + "/create", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userName: "john_doe",
                userKey: "key123",
                firstName: "John",
                lastName: "Doe",
                email: "john@example.com"
            })
        });
        console.log("Response status: res2.status =", res2.status);
        console.log("Response status: res2.statusText =", res2.statusText);
        const json2 = await res2.json();
        console.log("Response JSON:", json2);
        output.innerHTML += JSON.stringify(json2, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 2:", err);
        output.innerHTML += "Error in Test 2: " + err + "\n\n";
    }

    // 3️⃣ Get User by userName
    try {
        console.log("Test 3: Get User by userName");
        output.innerHTML += "Test 3: Get User by userName\n";
        const res3 = await fetch(API_BASE + "/get?userName=john_doe", { method: 'GET' });
        console.log("Response status: res3.status =", res3.status);
        console.log("Response status: res3.statusText =", res3.statusText);
        const json3 = await res3.json();
        console.log("Response JSON:", json3);
        output.innerHTML += JSON.stringify(json3, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 3:", err);
        output.innerHTML += "Error in Test 3: " + err + "\n\n";
    }

    // 4️⃣ Update User by userName
    try {
        console.log("Test 4: Update User by userName");
        output.innerHTML += "Test 4: Update User by userName\n";
        const res4 = await fetch(API_BASE + "/update?userName=john_doe", {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ firstName: "Johnny", email: "johnny@example.com" })
        });
        console.log("Response status: res4.status =", res4.status);
        console.log("Response status: res4.statusText =", res4.statusText);
        const json4 = await res4.json();
        console.log("Response JSON:", json4);
        output.innerHTML += JSON.stringify(json4, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 4:", err);
        output.innerHTML += "Error in Test 4: " + err + "\n\n";
    }

    // 5️⃣ Delete User by userName
    try {
        console.log("Test 5: Delete User by userName");
        output.innerHTML += "Test 5: Delete User by userName\n";
        const res5 = await fetch(API_BASE + "/delete?userName=john_doe", { method: 'DELETE' });
        console.log("Response status: res5.status =", res5.status);
        console.log("Response status: res5.statusText =", res5.statusText);
        const json5 = await res5.json();
        console.log("Response JSON:", json5);
        output.innerHTML += JSON.stringify(json5, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 5:", err);
        output.innerHTML += "Error in Test 5: " + err + "\n\n";
    }

    // 6️⃣ Optional: Search with filters
    try {
        console.log("Test 6: Search Users with filter firstName=John");
        output.innerHTML += "Test 6: Search Users with filter firstName=John\n";
        const res6 = await fetch(API_BASE + "?firstName=John&city=New+York&page=0&size=5", { method: 'GET' });
        console.log("Response status: res6.status =", res6.status);
        console.log("Response status: res6.statusText =", res6.statusText);
        const json6 = await res6.json();
        console.log("Response JSON:", json6);
        output.innerHTML += JSON.stringify(json6, null, 2) + "\n\n";
    } catch (err) {
        console.error("Error in Test 6:", err);
        output.innerHTML += "Error in Test 6: " + err + "\n\n";
    }

    console.log("---------- End of tests ----------");
    output.innerHTML += "---------- End of tests ----------\n";
}

testUserAPI();
</script>
</body>
</html>
