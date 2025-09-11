<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Users List</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="../../validation/validation.js"></script>
    <script src="user.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
    <style>
        .error-message { color: red; font-size: 0.9em; }
        .invalid { border: 1px solid red; }
        .form-columns { display: flex; gap: 20px; }
        .form-col { flex: 1; }
        .form-row { margin-bottom: 8px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Users List</h1>

    <!-- Search Form -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 800px;">
        <form data-bind="submit: searchFormVM.save">
            <div class="form-columns">
                <fieldset class="form-col">
                    <legend>Core Info</legend>
                    <!-- ko foreach: searchFormVM.formConfig.fields.slice(0,3) -->
                    <div class="form-row" data-bind="visible: visible">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.currentData[name], valueUpdate: 'input', css: { invalid: $parent.errorMessages()[name] }">
                        </label>
                        <span class="error-message" data-bind="text: $parent.errorMessages()[name]"></span>
                    </div>
                    <!-- /ko -->
                </fieldset>

                <fieldset class="form-col">
                    <legend>Address Info</legend>
                    <!-- ko foreach: searchFormVM.formConfig.fields.slice(3) -->
                    <div class="form-row" data-bind="visible: visible">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.currentData[name], valueUpdate: 'input', css: { invalid: $parent.errorMessages()[name] }">
                        </label>
                        <span class="error-message" data-bind="text: $parent.errorMessages()[name]"></span>
                    </div>
                    <!-- /ko -->
                </fieldset>
            </div>

            <div class="form-actions" style="margin-top: 10px;">
                <button type="submit">Search</button>
                <button type="button" data-bind="click: searchFormVM.cancel">Reset</button>
            </div>
        </form>
        <div style="margin-top: 10px;">
            <a href="#" data-bind="click: userVM.goAddUser">Create User</a>
        </div>
    </div>

    <!-- Users Table -->
    <div data-bind="with: userVM">
        <table border="1" cellpadding="5" cellspacing="0">
            <thead>
            <tr data-bind="foreach: gridConfig.columns">
                <th data-bind="text: label, 
                               click: function() { if(name !== 'actions') $parent.setSort(name) }, 
                               style: { cursor: name !== 'actions' ? 'pointer' : 'default' }"></th>
            </tr>
            </thead>
            <tbody data-bind="foreach: users">
            <tr data-bind="foreach: $parent.gridConfig.columns">
                <!-- ko if: name === 'actions' -->
                <td>
                    <a href="#" data-bind="click: function() { $parents[1].goEditUser($parent.id) }">Edit</a> |
                    <a href="#" data-bind="click: function() { $parents[1].deleteUser($parent) }">Delete</a>
                </td>
                <!-- /ko -->
                <!-- ko if: name !== 'actions' -->
                <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
                <!-- /ko -->
            </tr>
            </tbody>
        </table>

        <div style="margin-top: 10px;">
            <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
            <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
            <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
        </div>
    </div>
</div>

<script>
console.log("➡ Fetching config from /chatgpt/api/mybatis/config/all ...");

fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        const data = cfg.data;
        const gridConfig = data.grids.find(g => g.id === 'users');
        const searchConfig = data.forms.find(f => f.id === 'searchUser');
        const regexMap = (data.regexes || []).reduce((map, r) => { map[r.id] = r; return map; }, {});

        // UserViewModel instance
        const userVM = new UserViewModel({ grid: gridConfig, search: searchConfig });

        // ConfigDrivenViewModel for search validation
        const searchFormVM = new ConfigDrivenViewModel(searchConfig, regexMap, {
            onSave: function(searchParams) {
                // Apply validated search params to UserViewModel
                searchConfig.fields.forEach(f => userVM.searchParams[f.name](searchParams[f.name] || ''));
                userVM.page(1);
                userVM.loadUsers();
            },
            onCancel: function() {
                searchConfig.fields.forEach(f => userVM.searchParams[f.name](''));
                userVM.page(1);
                userVM.loadUsers();
            }
        });

        ko.applyBindings({ userVM, searchFormVM });
    })
    .catch(err => console.error("❌ Fetch error: ", err));
</script>
</body>
</html>
