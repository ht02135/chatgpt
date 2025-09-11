<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="../../validation/validation.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Add User</h1>
    <form data-bind="submit: saveUser">
        <div class="form-vertical" data-bind="foreach: formConfig.fields">
            <div class="form-row">
                <label data-bind="text: label + ':'"></label>
                <input type="text" data-bind="value: $parent.currentUser()[name], enable: editable" />
                <div class="error-message" data-bind="text: $parent.errors()[name], visible: $parent.errors()[name]"></div>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" id="submitBtn">Save</button>
            <button type="button" id="cancelBtn" data-bind="click: goUsers">Cancel</button>
        </div>
    </form>
</div>

<script>
(async function(){
    try {
        const formConfig = await configLoader.getFormConfig('addUser');
        const regexConfig = await configLoader.getRegexConfig();

        const userVM = new UserViewModel({ mode: 'add' }, { form: formConfig });
        userVM.validator = new Validator(regexConfig);
        userVM.errors = ko.observable({});
        ko.applyBindings({ userVM });
    } catch(e) {
        console.error("❌ Add User page init error:", e);
    }
})();
</script>
</body>
</html>
