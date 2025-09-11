<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
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
<div class="container" data-bind="with: $root">
    <h1>Edit User</h1>

    <div data-bind="if: editFormVM">
        <form data-bind="submit: editFormVM.save">
            <div data-bind="foreach: editFormVM.formConfig.fields">
                <div class="form-row">
                    <label data-bind="text: label + ':'"></label>
                    <input type="text" data-bind="
                        value: $parent.editFormVM.currentData[name],
                        css: { invalid: $parent.editFormVM.errorMessages()[name] },
                        valueUpdate: 'input',
                        enable: editable
                    " />
                    <span class="error-message" data-bind="text: $parent.editFormVM.errorMessages()[name]"></span>
                </div>
            </div>
            <button type="submit">Save</button>
            <button type="button" data-bind="click: editFormVM.cancel">Cancel</button>
        </form>
    </div>
</div>

<script>
fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        const data = cfg.data;
        const formConfig = data.forms.find(f => f.id === 'editUser');

        const userVM = new UserViewModel({
            grid: null,
            form: formConfig,
            search: null
        });

        userVM.editFormVM = new ConfigDrivenViewModel(formConfig, {}, {
            onSave: data => userVM.saveUser(data),
            onCancel: () => userVM.goUsers(),
            searchTargetVM: userVM.currentUser
        });

        // Load the edit ID from localStorage
        const editId = localStorage.getItem('editUserId');
        if (editId) {
            userVM.loadUserById(editId);
        }

        ko.applyBindings(userVM);
    })
    .catch(err => console.error("❌ Fetch error:", err));
</script>
</body>
</html>
