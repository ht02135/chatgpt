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
                <input type="text" data-bind="value: $parent.currentUser()[name], enable: editable, valueUpdate: 'input'" />
                <div class="error-message" 
                     data-bind="text: $root.userVM.errors()[name], 
                                visible: $root.userVM.errors()[name]"></div>
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

    const userVM = new UserViewModel({ mode: 'edit' }, { form: formConfig });
    userVM.validator = new Validator(regexConfig);
    userVM.errors = ko.observable({});

    // Load user by ID
    const editId = localStorage.getItem('editUserId');
    if(editId) await userVM.loadUserById(editId);

    // Validate on typing
    formConfig.fields.forEach(f => {
        if(userVM.currentUser()[f.name]) {
            userVM.currentUser()[f.name].subscribe(() => {
                const errs = userVM.validator.validateForm(userVM.currentUser(), formConfig.fields);
                userVM.errors(errs);
            });
        }
    });

    ko.applyBindings({ userVM });
})();
</script>
</body>
</html>
