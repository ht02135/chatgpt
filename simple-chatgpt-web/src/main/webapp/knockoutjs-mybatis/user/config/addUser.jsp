<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="user.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Add User</h1>
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
    console.log("➡ Fetching config from /chatgpt/api/mybatis/config/all ...");
    fetch('/chatgpt/api/mybatis/config/all')
        .then(res => res.json())
        .then(cfg => {
            console.log("✅ Full config response: ", cfg);
            const data = cfg.data; // Extract the data object first
            console.log("➡ Config data: ", data);
            console.log("➡ Available forms: ", data.forms);
            
            const formConfig = data.forms.find(f => f.id === 'addUser'); // Use data.forms instead of cfg.forms
            console.log("➡ Selected formConfig: ", formConfig);
            
            if (!formConfig) {
                console.error("❌ Form config 'addUser' not found!");
                return;
            }
            
            const userVM = new UserViewModel({ mode: 'add' }, { form: formConfig });
            console.log("✅ UserViewModel created: ", userVM);
            ko.applyBindings({ userVM });
        })
        .catch(err => console.error("❌ Fetch error: ", err));
</script>
</body>
</html>