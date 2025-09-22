<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test UserManagementList API</title>
<style>
body { font-family: Arial; margin: 20px; }
pre { background: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }
button { margin: 5px; padding: 8px 12px; }
</style>
</head>
<body>
<h1>Test UserManagementList API</h1>
<pre id="output"></pre>

<script>
const output = document.getElementById("output");
const API_BASE = '/chatgpt/api/management/userlists';
const UPLOAD_CSV = '/chatgpt/test-management/upload/test_user_lists_1.csv';
const UPLOAD_XLS = '/chatgpt/test-management/upload/test_user_lists_1.xls';

// Utility to append to output
function log(msg) {
    output.innerHTML += msg + "\n";
}

// ------------------ TEST FUNCTIONS ------------------

// 1️⃣ Create List with members
async function testCreateList() {
    log("=== Test 1: Create List ===");
    const list = { userListName: "Test List", description: "List for JSP test" };
    const members = [
        { 
            userName: "alice", password: "ZAQ!zaq1", firstName: "Alice", lastName: "Wonder",
            email: "alice@example.com", addressLine1: "123 Main St", addressLine2: "Apt 4",
            city: "Wonderland", state: "WL", postCode: "12345", country: "Fantasia"
        },
        { 
            userName: "bob", password: "ZAQ!ZAQ!", firstName: "Bob", lastName: "Builder",
            email: "bob@example.com", addressLine1: "456 Build Rd", addressLine2: "",
            city: "Construct", state: "CN", postCode: "67890", country: "Builderland"
        }
    ];

    try {
        const formData = new FormData();
        formData.append("list", new Blob([JSON.stringify(list)], { type: "application/json" }));
        formData.append("members", new Blob([JSON.stringify(members)], { type: "application/json" }));

        const res = await fetch(API_BASE + "/create", { method: 'POST', body: formData });
        const json = await res.json();
        log(JSON.stringify(json, null, 2));
    } catch(e) {
        log("Error: " + e);
    }
}

// 2️⃣ Get List by ID
async function testGetList(listId) {
    log("=== Test 2: Get List by ID ===");
    try {
        const res = await fetch(API_BASE + "/get?id=" + listId);
        const json = await res.json();
        log(JSON.stringify(json, null, 2));
    } catch(e) {
        log("Error: " + e);
    }
}

// 3️⃣ Get Members of List
async function testGetMembers(listId) {
    log("=== Test 3: Get Members ===");
    try {
        const res = await fetch(API_BASE + "/members?listId=" + listId);
        const json = await res.json();
        log(JSON.stringify(json, null, 2));
    } catch(e) {
        log("Error: " + e);
    }
}

// 4️⃣ Import CSV
async function testImportCsv() {
    log("=== Test 4: Import CSV ===");
    try {
        const fileRes = await fetch(UPLOAD_CSV);
        const blob = await fileRes.blob();

        const list = { userListName: "CSV Import List", description: "From CSV file" };
        const formData = new FormData();
        formData.append("list", new Blob([JSON.stringify(list)], { type: "application/json" }));
        formData.append("file", blob, "test_user_lists_1.csv");

        const res = await fetch(API_BASE + "/import", { method: 'POST', body: formData });
        const json = await res.json();
        log(JSON.stringify(json, null, 2));
        return json.data.id;
    } catch(e) {
        log("Error: " + e);
    }
}

// 5️⃣ Import Excel
async function testImportExcel() {
    log("=== Test 5: Import Excel ===");
    try {
        const fileRes = await fetch(UPLOAD_XLS);
        const blob = await fileRes.blob();

        const list = { userListName: "Excel Import List", description: "From XLS file" };
        const formData = new FormData();
        formData.append("list", new Blob([JSON.stringify(list)], { type: "application/json" }));
        formData.append("file", blob, "test_user_lists_1.xls");

        const res = await fetch(API_BASE + "/import", { method: 'POST', body: formData });
        const json = await res.json();
        log(JSON.stringify(json, null, 2));
        return json.data.id;
    } catch(e) {
        log("Error: " + e);
    }
}

// 6️⃣ Export CSV
async function testExportCsv(listId) {
    log("=== Test 6: Export CSV ===");
    try {
        const res = await fetch(API_BASE + "/export/csv?listId=" + listId);
        const blob = await res.blob();
        const filename = "export_" + listId + ".csv";

        const a = document.createElement("a");
        a.href = URL.createObjectURL(blob);
        a.download = filename;
        a.click();
        log("Exported CSV saved as " + filename);
    } catch(e) {
        log("Error: " + e);
    }
}

// 7️⃣ Export Excel
async function testExportExcel(listId) {
    log("=== Test 7: Export Excel ===");
    try {
        const res = await fetch(API_BASE + "/export/excel?listId=" + listId);
        const blob = await res.blob();
        const filename = "export_" + listId + ".xlsx";

        const a = document.createElement("a");
        a.href = URL.createObjectURL(blob);
        a.download = filename;
        a.click();
        log("Exported Excel saved as " + filename);
    } catch(e) {
        log("Error: " + e);
    }
}

// 8️⃣ Delete List
async function testDeleteList(listId) {
    log("=== Test 8: Delete List ===");
    try {
        const res = await fetch(API_BASE + "/delete?listId=" + listId, { method: 'DELETE' });
        const json = await res.json();
        log(JSON.stringify(json, null, 2));
    } catch(e) {
        log("Error: " + e);
    }
}

// ------------------ RUN TESTS ------------------
(async function runTests() {
    await testCreateList();
    let csvListId = await testImportCsv();
    let xlsListId = await testImportExcel();

    if(csvListId) {
        await testGetList(csvListId);
        await testGetMembers(csvListId);
        await testExportCsv(csvListId);
        await testExportExcel(csvListId);
        await testDeleteList(csvListId);
    }

    if(xlsListId) {
        await testGetList(xlsListId);
        await testGetMembers(xlsListId);
        await testExportCsv(xlsListId);
        await testExportExcel(xlsListId);
        await testDeleteList(xlsListId);
    }

    log("=== All tests finished ===");
})();
</script>
</body>
</html>
