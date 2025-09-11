<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title data-bind="text: mode==='add'?'Add User':'Edit User'">Add/Edit User</title>
<script src="../../../js/knockout-latest.js"></script>
<script src="configLoader.js"></script>
<script src="../../validation/validation.js"></script>
<script src="user.js"></script>
<link rel="stylesheet" href="../../../css/user.css">
<style>.error-message{color:red;font-size:0.9em;}</style>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1 data-bind="text: mode==='add'?'Add User':'Edit User'"></h1>
    <form data-bind="submit: saveUser">
        <div data-bind="foreach: formConfig.fields">
            <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label+':'"></label>
                <input data-bind="value: $parent.currentUser()[name], enable: editable">
                <div class="error-message" data-bind="text: $parent.errors()[name], visible: $parent.errors()[name]"></div>
            </div>
        </div>
        <button type="submit">Save</button>
        <button type="button" data-bind="click: goUsers">Cancel</button>
    </form>
</div>

<script>
(async function(){
    const cfg = await loadConfig();
    const formConfig = cfg.forms.find(f=>f.id=== (window.location.href.includes('edit')?'editUser':'addUser'));
    const regexes = cfg.regex;
    const mode = window.location.href.includes('edit')?'edit':'add';

    const userVM = new UserViewModel({mode}, {form: formConfig}, regexes);
    window.userVM = userVM;
    ko.applyBindings({userVM});
})();
</script>
</body>
</html>
