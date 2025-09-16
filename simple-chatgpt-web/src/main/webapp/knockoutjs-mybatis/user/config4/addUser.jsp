<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="../../../js/validation.js"></script>
    <script src="../../../js/configLoader.js"></script>
    <script src="user.js"></script>
	<!--
	<script src="genericFormTitleComponent.js"></script>
	<script src="genericFormFieldsComponent.js"></script>
	<script src="genericFormActionsComponent.js"></script>
    <script src="genericComposedFormComponent.js"></script>
	-->
	<script src="../../../js/genericComponents.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
    <!-- Use the reusable component -->
    <generic-composed-form params="vm: userVM"></generic-composed-form>

<script>
(async function(){
    const formConfig = await configLoader.getFormConfig('addUser');
    const regexConfig = await configLoader.getRegexMapConfig();

    const userVM = new UserViewModel({ mode: 'add' }, { form: formConfig });
    userVM.validator = new Validator(regexConfig);
    userVM.errors = ko.observable({});

    // Initialize currentUser
    userVM.currentUser(new User({}, formConfig.fields));

    ko.applyBindings({ userVM });
})();
</script>
</body>
</html>
