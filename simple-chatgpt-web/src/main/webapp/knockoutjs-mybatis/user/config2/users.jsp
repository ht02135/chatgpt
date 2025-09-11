<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Users</title>
    <script src="../../../js/knockout-latest.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
    <script src="user.js"></script>
    <script src="../../validation/validation.js"></script>
</head>
<body>
<div class="container">

    <h1>Users List</h1>

    <!-- Search Form -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 600px;">
        <form data-bind="submit: searchFormVM.save">
            <div class="form-columns">

                <!-- Core Section -->
                <fieldset class="form-col">
                    <legend>Core Section</legend>
                    <!-- ko foreach: searchFormVM.formConfig.fields.slice(0,3) -->
                    <div class="form-row" data-bind="visible: visible">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.currentData[name], valueUpdate: 'input'">
                        </label>
                        <span style="color:red" data-bind="text: $parent.errorMessages()[name]"></span>
                    </div>
                    <!-- /ko -->
                </fieldset>

                <!-- Additional Address Section -->
                <fieldset class="form-col">
                    <legend>Additional Address</legend>
                    <!-- ko foreach: searchFormVM.formConfig.fields.slice(3) -->
                    <div class="form-row" data-bind="visible: visible">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.currentData[name], valueUpdate: 'input'">
                        </label>
                        <span style="color:red" data-bind="text: $parent.errorMessages()[name]"></span>
                    </div>
                    <!-- /ko -->
                </fieldset>

            </div>
        </form>

        <!-- Actions -->
        <div class="form-actions">
            <a href="#" data-bind="click: goAddUser">Create User</a>
            <a href="#" data-bind="click: searchFormVM.save" style="margin-left:20px;">Search</a>
            <a href="#" data-bind="click: searchFormVM.cancel" style="margin-left:20px;">Reset</a>
        </div>
    </div>

    <!-- Users Table -->
    <table>
        <thead>
            <tr data-bind="foreach: gridConfig.columns">
                <th data-bind="text: label, 
                               click: function() { if(name !== 'actions') $parent.setSort(name) }, 
                               style: { cursor: name !== 'actions' ? 'pointer' : 'default' }"></th>
            </tr>
        </thead>
        <tbody data-bind="foreach: users">
            <tr data-bind="foreach: $parent.gridConfig.columns">
                <!-- Actions Column -->
                <!-- ko if: name === 'actions' -->
                <td>
                    <a href="#" data-bind="click: function() { $parents[1].goEditUser($parent.id) }">Edit</a> |
                    <a href="#" data-bind="click: function() { $parents[1].deleteUser($parent) }">Delete</a>
                </td>
                <!-- /ko -->
                <!-- Data Columns -->
                <!-- ko if: name !== 'actions' -->
                <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
                <!-- /ko -->
            </tr>
        </tbody>
    </table>

    <!-- Pagination -->
    <div style="margin-top:10px;">
        <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
    </div>
</div>

<script>
fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        const data = cfg.data;
        const gridConfig = data.grids.find(g => g.id === 'users');
        const searchConfig = data.forms.find(f => f.id === 'searchUser');

        // Create UserViewModel
        const userVM = new UserViewModel({ grid: gridConfig, search: searchConfig });

        // Link search form with searchParams
        const searchFormVM = new ConfigDrivenViewModel(searchConfig, {}, {
            searchTargetVM: userVM.searchParams
        });

        // Apply bindings
        userVM.searchFormVM = searchFormVM;
        ko.applyBindings(userVM);
    })
    .catch(err => console.error("Fetch error:", err));
</script>
</body>
</html>
