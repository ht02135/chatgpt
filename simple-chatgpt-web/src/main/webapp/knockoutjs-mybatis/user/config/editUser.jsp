<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="user.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Edit User</h1>
    <form data-bind="submit: saveUser">
        <div data-bind="foreach: formConfig.fields">
            <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label + ':'"></label>
                <input data-bind="value: $parent.currentUser()[name], enable: editable" required />
            </div>
        </div>
        <button type="submit">Save</button>
        <button type="button" data-bind="click: goUsers">Cancel</button>
    </form>
</div>
<script>
    fetch('/chatgpt/api/mybatis/config/all')
        .then(res => res.json())
        .then(cfg => {
            const formConfig = cfg.forms.find(f => f.id === 'editUser');
            const userVM = new UserViewModel({ mode: 'edit' }, { form: formConfig });
            ko.applyBindings({ userVM });
        });
</script>
</body>
</html>