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
<div class="container" data-bind="with: userVM">
    <h1>Add User</h1>
    <form data-bind="submit: save">
        <div data-bind="foreach: formConfig.fields">
            <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label + ':'"></label>
                <input data-bind="
                    value: currentData[name],
                    css: { invalid: errorMessages()[name] }
                " required />
                <span class="error-message" data-bind="text: errorMessages()[name]"></span>
            </div>
        </div>
        <button type="submit">Save</button>
        <button type="button" data-bind="click: cancel">Cancel</button>
    </form>
</div>

<script>
fetch('/chatgpt/api/mybatis/config/all')
.then(res => res.json())
.then(cfg => {
    const data = cfg.data;
    const formConfig = data.forms.find(f => f.id === 'addUser');
    if (!formConfig) { console.error("❌ Form config 'addUser' not found!"); return; }

    const regexMap = (data.regexes || []).reduce((m,r)=>{ m[r.id]=r; return m; }, {});

    // Instantiate UserViewModel for saveUser
    const userVM = new UserViewModel({ grid: null, form: formConfig });

    // Attach validation VM
    const formVM = new ConfigDrivenViewModel(formConfig, regexMap, {
        onSave: (formData) => userVM.saveUser(formData),
        onCancel: () => window.location.href='users.jsp'
    });

    // Merge formConfig and observables for KO
    formVM.formConfig = formConfig;
    ko.applyBindings({ userVM: formVM });
});
</script>
</body>
</html>
