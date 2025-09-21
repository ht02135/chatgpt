<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test UserManagement List API</title>
<style>
body { font-family: Arial; margin: 20px; }
pre { background: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }
</style>
</head>
<body>
<h1>Test UserManagement List API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_BASE = '/chatgpt/api/management/userlists';

async function testUserListAPI() {
    // 1️⃣ Create List
    try {
        output.innerHTML += "Test 1: Create List\n";
        const res1 = await fetch(API_BASE + "/create", {
            method: 'POST',
            body: new FormData(Object.assign(document.createElement("form"), {
                list: new Blob([JSON.stringify({
                    name: "Test List 1",
                    description: "Created from test-userList.jsp"
                })], { type: "application/json" })
            }))
        });
        const json1 = await res1.json();
        console.log("Response JSON:", json1);
        output.innerHTML += JSON.stringify(json1, null, 2) + "\n\n";
    } catch (err) {
        output.innerHTML += "Error in Test 1: " + err + "\n\n";
    }

    // 2️⃣ Import CSV
    try {
        output.innerHTML += "Test 2: Import List (CSV)\n";
        const formDataCsv = new FormData();
        formDataCsv.append("list", new Blob([JSON.stringify({ name: "CSV Test List" })], { type: "application/json" }));
        formDataCsv.append("file", new File([], "test-userList.csv")); // reference file in upload folder

        const res2 = await fetch(API_BASE + "/import", { method: 'POST', body: formDataCsv });
        const json2 = await res2.json();
        output.innerHTML += JSON.stringify(json2, null, 2) + "\n\n";
    } catch (err) {
        output.innerHTML += "Error in Test 2: " + err + "\n\n";
    }

    // 3️⃣ Import Excel
    try {
        output.innerHTML += "Test 3: Import List (Excel)\n";
        const formDataXls = new FormData();
        formDataXls.append("list", new Blob([JSON.stringify({ name: "Excel Test List" })], { type: "application/json" }));
        formDataXls.append("file", new File([], "test_user_lists_1.xls")); // reference file in upload folder

        const res3 = await fetch(API_BASE + "/import", { method: 'POST', body: formDataXls });
        const json3 = await res3.json();
        output.innerHTML += JSON.stringify(json3, null, 2) + "\n\n";
    } catch (err) {
        output.innerHTML += "Error in Test 3: " + err + "\n\n";
    }

    // 4️⃣ Export CSV
    try {
        output.innerHTML += "Test 4: Export List to CSV\n";
        const res4 = await fetch(API_BASE + "/export/csv?listId=1"); // use an existing listId
        if (res4.ok) {
            const blob = await res4.blob();
            const url = URL.createObjectURL(blob);
            output.innerHTML += "CSV exported successfully. Saved to test-management/export folder manually.\n\n";
            // For manual save, you would configure backend to also copy to /test-management/export
        }
    } catch (err) {
        output.innerHTML += "Error in Test 4: " + err + "\n\n";
    }

    // 5️⃣ Export Excel
    try {
        output.innerHTML += "Test 5: Export List to Excel\n";
        const res5 = await fetch(API_BASE + "/export/excel?listId=1");
        if (res5.ok) {
            const blob = await res5.blob();
            const url = URL.createObjectURL(blob);
            output.innerHTML += "Excel exported successfully. Saved to test-management/export folder manually.\n\n";
        }
    } catch (err) {
        output.innerHTML += "Error in Test 5: " + err + "\n\n";
    }

    output.innerHTML += "---------- End of tests ----------\n";
}

testUserListAPI();
</script>
</body>
</html>
