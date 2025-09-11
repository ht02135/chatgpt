<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <script src="../../validation/validation.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<div class="container" data-bind="with: $root">
    <h1>Add User</h1>

    <form class="form-vertical">
        <div data-bind="foreach: formConfig.fields">
            <div class="form-row">
                <label data-bind="text: label + ':'"></label>
                <input type="text" data-bind="value: $parent.userVM.userData[name]">
                <div class="error-message" data-bind="text: $parent.userVM.errors()[name], visible: $parent.userVM.errors()[name]"></div>
            </div>
        </div>

        <div class="form-actions">
            <a href="#" data-bind="click: userVM.saveUser">Save</a>
            <a href="#" data-bind="click: userVM.cancelUser" style="margin-left:20px;">Cancel</a>
        </div>
    </form>
</div>

<script>
(async function() {
    try {
        const formConfig = await configLoader.getFormConfig('addUser');
        const regexConfig = await configLoader.getRegexConfig();

        const userVM = new UserViewModel({ mode: 'add' }, { form: formConfig });
        userVM.validator = new Validator(regexConfig);
        userVM.errors = ko.observable({});

        // Initialize dynamic userData observables
        userVM.userData = {};
        formConfig.fields.forEach(f => userVM.userData[f.name] = ko.observable(''));

        ko.applyBindings({ userVM, formConfig });
    } catch (e) {
        console.error("❌ Failed to initialize Add User page:", e);
    }
})();
</script>
</body>
</html>
