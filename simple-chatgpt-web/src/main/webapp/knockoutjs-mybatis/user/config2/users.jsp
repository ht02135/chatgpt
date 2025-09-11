<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Users Management</title>
<script src="../../../js/knockout-latest.js"></script>
<script src="user.js"></script>
<script src="../../validation/validation.js"></script>
<link rel="stylesheet" href="../../../css/user.css">
<style>
  .form-columns { display: flex; gap: 20px; }
  .form-col { flex: 1; }
  .form-row { margin-bottom: 10px; }
  .form-actions { margin-top: 10px; }
</style>
</head>
<body>

<div class="container" data-bind="with: $root">
    <h1>Users List</h1>

    <!-- Search Form -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 600px;" data-bind="if: searchFormVM">
        <form data-bind="submit: searchFormVM.save">
            <div class="form-columns">

                <!-- Core Section -->
                <fieldset class="form-col">
                    <legend>Core Section</legend>
                    <!-- ko foreach: searchFormVM.formConfig.fields.slice(0,3) -->
                    <div class="form-row">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.searchFormVM.currentData[name], valueUpdate: 'input'">
                        </label>
                    </div>
                    <!-- /ko -->
                </fieldset>

                <!-- Additional Section -->
                <fieldset class="form-col">
                    <legend>Additional Address</legend>
                    <!-- ko foreach: searchFormVM.formConfig.fields.slice(3) -->
                    <div class="form-row">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.searchFormVM.currentData[name], valueUpdate: 'input'">
                        </label>
                    </div>
                    <!-- /ko -->
                </fieldset>

            </div>
        </form>

        <div class="form-actions">
            <a href="#" data-bind="click: goAddUser">Create User</a>
            <a href="#" data-bind="click: searchFormVM.save" style="margin-left: 20px;">Search</a>
            <a href="#" data-bind="click: searchFormVM.cancel" style="margin-left: 20px;">Reset</a>
        </div>
    </div>

    <!-- Users Table -->
    <table border="1" cellpadding="5">
        <thead>
            <tr data-bind="foreach: gridConfig ? gridConfig.columns : []">
                <th data-bind="text: label,
                               click: function() { if(name !== 'actions') $parent.setSort(name) },
                               style: { cursor: name !== 'actions' ? 'pointer' : 'default' }"></th>
            </tr>
        </thead>
        <tbody data-bind="foreach: users">
            <tr data-bind="foreach: $parent.gridConfig ? $parent.gridConfig.columns : []">
                <!-- Actions column -->
                <!-- ko if: name === 'actions' -->
                <td>
                    <a href="#" data-bind="click: function() { $parents[1].goEditUser($parent.id) }">Edit</a> |
                    <a href="#" data-bind="click: function() { $parents[1].deleteUser($parent) }">Delete</a>
                </td>
                <!-- /ko -->
                <!-- Data columns -->
                <!-- ko if: name !== 'actions' -->
                <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
                <!-- /ko -->
            </tr>
        </tbody>
    </table>

    <!-- Pagination -->
    <div style="margin-top: 10px;">
        <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
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
        const formConfig = data.forms.find(f => f.id === 'addUser'); // For add/edit if needed

        // Create main UserViewModel
        const userVM = new UserViewModel({
            grid: gridConfig,
            form: formConfig,
            search: searchConfig
        });

        // Link search form dynamically to userVM.searchParams
        if (searchConfig) {
            userVM.searchFormVM = new ConfigDrivenViewModel(searchConfig, {}, {
                searchTargetVM: userVM.searchParams
            });
        }

        // Apply bindings
        ko.applyBindings(userVM);

        // Initial load
        if (typeof userVM.loadUsers === 'function') {
            userVM.loadUsers();
        }
    })
    .catch(err => console.error("❌ Fetch error: ", err));
</script>

</body>
</html>
