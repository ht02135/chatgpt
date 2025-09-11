<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
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
    <h1>Add User</h1>

    <div data-bind="if: addFormVM">
        <form data-bind="submit: addFormVM.save">
            <div data-bind="foreach: addFormVM.formConfig.fields">
                <div class="form-row">
                    <label data-bind="text: label + ':'"></label>
                    <input type="text" data-bind="
                        value: $parent.addFormVM.currentData[name],
                        css: { invalid: $parent.addFormVM.errorMessages()[name] },
                        valueUpdate: 'input'
                    " />
                    <span class="error-message" data-bind="text: $parent.addFormVM.errorMessages()[name]"></span>
                </div>
            </div>
            <button type="submit">Save</button>
            <button type="button" data-bind="click: addFormVM.cancel">Cancel</button>
        </form>
    </div>
</div>

<script>
fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        const data = cfg.data;
        const formConfig = data.forms.find(f => f.id === 'addUser');

        const userVM = new UserViewModel({
            grid: null,
            form: formConfig,
            search: null
        });

        // Link addFormVM to currentUser observables and validations
        userVM.addFormVM = new ConfigDrivenViewModel(formConfig, {}, {
            onSave: data => userVM.saveUser(data),
            onCancel: () => userVM.goUsers(),
            searchTargetVM: userVM.currentUser
        });

        ko.applyBindings(userVM);
    })
    .catch(err => console.error("❌ Fetch error:", err));
</script>
</body>
</html>
