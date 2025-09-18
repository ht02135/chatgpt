<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test Management Config API</title>
<style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h2 { margin-top: 30px; }
    pre { background: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }
    table { border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 5px 10px; text-align: left; }
    th { background: #eee; }
</style>
</head>
<body>
<h1>Test Management Config API</h1>

<div id="output"></div>

<script>
const output = document.getElementById("output");
const API_CONFIG = '/chatgpt/api/management/config';

async function testConfig() {
    try {
        const res = await fetch(API_CONFIG, { method: 'GET' });
        const json = await res.json();

        if (!json.data) {
            output.innerHTML = '<p style="color:red;">Error: no config data returned</p>';
            return;
        }

        const config = json.data;

        // Display summary
        output.innerHTML += `<h2>Status</h2><pre>${json.status}</pre>`;
        output.innerHTML += `<h2>Message</h2><pre>${json.message}</pre>`;
        output.innerHTML += `<h2>Timestamp</h2><pre>${json.timestamp}</pre>`;

        // Render grids
        if (config.grids && config.grids.length > 0) {
            output.innerHTML += `<h2>Grids</h2>`;
            config.grids.forEach(grid => {
                output.innerHTML += `<h3>Grid: ${grid.id}</h3>`;
                if (grid.columns && grid.columns.length > 0) {
                    let table = `<table><tr><th>Name</th><th>Label</th><th>Visible</th><th>Sortable</th><th>Actions</th></tr>`;
                    grid.columns.forEach(col => {
                        table += `<tr>
                            <td>${col.name}</td>
                            <td>${col.label}</td>
                            <td>${col.visible}</td>
                            <td>${col.sortable}</td>
                            <td>${col.actions || ''}</td>
                        </tr>`;
                    });
                    table += `</table>`;
                    output.innerHTML += table;
                }
            });
        }

        // Render actions
        if (config.actions && config.actions.length > 0) {
            output.innerHTML += `<h2>Actions</h2>`;
            config.actions.forEach(actionGroup => {
                output.innerHTML += `<h3>Action Group: ${actionGroup.id}</h3>`;
                if (actionGroup.actions && actionGroup.actions.length > 0) {
                    let table = `<table><tr><th>Name</th><th>Label</th><th>Visible</th><th>JS Method</th></tr>`;
                    actionGroup.actions.forEach(a => {
                        table += `<tr>
                            <td>${a.name}</td>
                            <td>${a.label}</td>
                            <td>${a.visible}</td>
                            <td>${a.jsMethod}</td>
                        </tr>`;
                    });
                    table += `</table>`;
                    output.innerHTML += table;
                }
            });
        }

        // Render regex
        if (config.regex && config.regex.length > 0) {
            output.innerHTML += `<h2>Regex</h2><pre>${JSON.stringify(config.regex, null, 2)}</pre>`;
        }

        // Render validators
        if (config.validators && config.validators.length > 0) {
            output.innerHTML += `<h2>Validators</h2><pre>${JSON.stringify(config.validators, null, 2)}</pre>`;
        }

        // Render forms
        if (config.forms && config.forms.length > 0) {
            output.innerHTML += `<h2>Forms</h2>`;
            config.forms.forEach(form => {
                output.innerHTML += `<h3>Form: ${form.id}</h3>`;
                if (form.fields && form.fields.length > 0) {
                    let table = `<table><tr><th>Name</th><th>Label</th><th>Visible</th><th>Required</th><th>Editable</th><th>Regex</th><th>ValidatorsId</th></tr>`;
                    form.fields.forEach(f => {
                        table += `<tr>
                            <td>${f.name}</td>
                            <td>${f.label}</td>
                            <td>${f.visible}</td>
                            <td>${f.required}</td>
                            <td>${f.editable}</td>
                            <td>${f.regex || ''}</td>
                            <td>${f.validatorsId || ''}</td>
                        </tr>`;
                    });
                    table += `</table>`;
                    output.innerHTML += table;
                }
            });
        }

    } catch (err) {
        console.error("Error fetching config:", err);
        output.innerHTML = `<p style="color:red;">Error fetching config: ${err}</p>`;
    }
}

testConfig();
</script>
</body>
</html>
