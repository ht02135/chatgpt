<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Users List</title>
<script src="../../../js/knockout-latest.js"></script>
<script src="user.js"></script>
<script src="../../validation/validation.js"></script>
<link rel="stylesheet" href="../../../css/user.css">
<style>
    .error-message { color: red; font-size: 0.9em; }
    .invalid { border: 1px solid red; }
</style>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Users List</h1>

    <!-- Search Form -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 600px;" data-bind="with: searchFormVM">
        <form data-bind="submit: save">
            <div data-bind="foreach: formConfig.fields">
                <div class="form-row" data-bind="visible: visible">
                    <label data-bind="text: label + ':'"></label>
                    <input data-bind="value: currentData[name], css: { invalid: errorMessages()[name] }" />
                    <span class="error-message" data-bind="text: errorMessages()[name]"></span>
                </div>
            </div>
            <button type="submit">Search</button>
            <button type="button" data-bind="click: cancel">Reset</button>
        </form>
    </div>

    <!-- Actions -->
    <div>
        <a href="#" data-bind="click: goAddUser">Create User</a>
    </div>

    <!-- Users Grid -->
    <table border="1" cellspacing="0" cellpadding="4">
        <thead>
            <tr data-bind="foreach: gridConfig.columns">
                <th data-bind="text: label, click: function() { if(name!=='actions') $parent.setSort(name) }, style: { cursor: name!=='actions' ? 'pointer' : 'default' }"></th>
            </tr>
        </thead>
        <tbody data-bind="foreach: users">
            <tr data-bind="foreach: $parent.gridConfig.columns">
                <!-- Actions Column -->
                <!-- ko if: name==='actions' -->
                <td>
                    <a href="#" data-bind="click: function() { $parents[1].goEditUser($parent.id) }">Edit</a> |
                    <a href="#" data-bind="click: function() { $parents[1].deleteUser($parent) }">Delete</a>
                </td>
                <!-- /ko -->
                <!-- Other Columns -->
                <!-- ko if: name!=='actions' -->
                <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
                <!-- /ko -->
            </tr>
        </tbody>
    </table>

    <!-- Pagination -->
    <div>
        <button data-bind="click: prevPage, enable: page()>1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page()<maxPage()">Next</button>
    </div>
</div>

<script>
fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        const data = cfg.data;
        const gridConfig = data.grids.find(g => g.id==='users');
        const searchConfig = data.forms.find(f => f.id==='searchUser');
        const regexMap = (data.regexes || []).reduce((map,r)=>{ map[r.id]=r; return map; }, {});

        const userVM = new UserViewModel({ grid: gridConfig, form: null, search: searchConfig });

        const searchFormVM = new ConfigDrivenViewModel(searchConfig, regexMap, {
            searchTargetVM: userVM.searchParams,
            onSave: () => { userVM.page(1); userVM.loadUsers(); },
            onCancel: () => { userVM.page(1); userVM.loadUsers(); }
        });

        userVM.searchFormVM = searchFormVM;
        window.userVM = userVM;
        ko.applyBindings(userVM);
    })
    .catch(err => console.error(err));
</script>
</body>
</html>
