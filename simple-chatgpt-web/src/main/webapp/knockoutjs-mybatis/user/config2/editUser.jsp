<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit User</title>
<script src="../../../js/knockout-latest.js"></script>
<script src="../../validation/validation.js"></script>
<script src="user.js"></script>
<script src="configLoader.js"></script>
<link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<div class="container">
    <h1>Edit User</h1>
    <form class="form-vertical" data-bind="submit: saveUser, visible: currentUser">
        <div data-bind="foreach: formConfig.fields">
            <div class="form-row">
                <label data-bind="text: label+':'"></label>
                <input type="text" data-bind="value: $parent.currentUser()[name], enable: editable"/>
                <div class="error-message" data-bind="text: $parent.errors()[name], visible:$parent.errors()[name]"></div>
            </div>
        </div>
        <button type="submit">Save</button>
        <button type="button" data-bind="click: goUsers">Cancel</button>
    </form>
</div>

<script>
(async function(){
    try {
        const formConfig = await configLoader.getFormConfig('editUser');
        const regexConfig = await configLoader.getRegexConfig();

        const userVM = new UserViewModel({mode:'edit'}, {form:formConfig});
        userVM.validator = new Validator(regexConfig);
        userVM.errors = ko.observable({});

        const editId = localStorage.getItem('editUserId');
        if(editId) await userVM.loadUserById(editId);

        ko.applyBindings(userVM);
    } catch(e){
        console.error("❌ Edit User init error:", e);
    }
})();
</script>
</body>
</html>
