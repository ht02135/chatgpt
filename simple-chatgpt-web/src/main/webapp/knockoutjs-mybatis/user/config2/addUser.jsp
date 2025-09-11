<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <script src="../../validation/validation.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
    <style>
        .invalid { border: 1px solid red; }
        .error-message { color: red; font-size: 0.9em; }
    </style>
</head>
<body>
<h2>Add User</h2>

<form data-bind="submit: $root.saveUser.bind($root, currentUser)">
    <div data-bind="foreach: $root.addFormConfig.fields">
        <div data-bind="if: visible">
            <label data-bind="text: label"></label>
            <input type="text" data-bind="value: $root.currentUser()[name], css: { invalid: $root.errors()[name] }, disable: !editable"/>
            <div class="error-message" data-bind="text: $root.errors()[name]"></div>
        </div>
    </div>
    <button type="submit">Save</button>
    <button type="button" data-bind="click: () => window.location.href='users.jsp'">Cancel</button>
</form>

<script>
(async function(){
    const config = await loadConfig();
    const vm = new UserViewModel(config);
    ko.applyBindings(vm);

    // Initialize empty user
    vm.currentUser(new User({}, vm.addFormConfig.fields));
})();
</script>
</body>
</html>
