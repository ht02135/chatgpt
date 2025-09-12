<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="validation.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Edit User</h1>
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
    const formConfig = await configLoader.getFormConfig('editUser');
    const regexConfig = await configLoader.getRegexConfig();
    const validator = new Validator(regexConfig);

    const userVM = new UserViewModel({ mode: 'edit' }, { form: formConfig }, validator);
    const editId = localStorage.getItem('editUserId');
    if(editId) await userVM.loadUserById(editId);

    ko.applyBindings({ userVM });
})();
</script>
</body>
</html>
