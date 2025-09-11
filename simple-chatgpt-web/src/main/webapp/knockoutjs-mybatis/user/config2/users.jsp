<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Users</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <script src="../../validation/validation.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<h2>User Management</h2>

<div data-bind="with: searchFormConfig">
    <form data-bind="submit: $root.searchUsers">
        <div data-bind="foreach: fields">
            <div data-bind="if: visible">
                <label data-bind="text: label"></label>
                <input type="text" data-bind="value: $root.searchParams[name], css: { invalid: $root.errors()[name] }"/>
                <div class="error-message" data-bind="text: $root.errors()[name]"></div>
            </div>
        </div>
        <button type="submit">Search</button>
        <button type="button" data-bind="click: $root.goAddUser">Add User</button>
    </form>
</div>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr data-bind="foreach: gridConfig.columns">
            <th data-bind="visible: visible, text: label"></th>
        </tr>
    </thead>
    <tbody data-bind="foreach: users">
        <tr>
            <td data-bind="text: id"></td>
            <td data-bind="text: firstName"></td>
            <td data-bind="text: lastName"></td>
            <td data-bind="text: email"></td>
            <td>
                <button data-bind="click: $root.goEditUser">Edit</button>
                <button data-bind="click: $root.deleteUser">Delete</button>
            </td>
        </tr>
    </tbody>
</table>

<script>
(async function(){
    const config = await loadConfig();
    const vm = new UserViewModel(config);
    ko.applyBindings(vm);
    vm.searchUsers();
})();
</script>
</body>
</html>
