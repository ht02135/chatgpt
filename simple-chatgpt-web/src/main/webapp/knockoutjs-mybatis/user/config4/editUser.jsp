<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="../../../js/validation.js"></script>
    <script src="../../../js/configLoader.js"></script>
    <script src="user.js"></script>
    <script src="../../../js/genericComponents.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
	<!-- Use the reusable component -->
	<generic-composed-form params="vm: userVM"></generic-composed-form>

    <script>
    (async function(){
        const formConfig = await configLoader.getFormConfig('editUser');
        const regexConfig = await configLoader.getRegexMapConfig();

        const userVM = new UserViewModel(
			{ mode: 'edit' }, 
			{ form: formConfig }
		);
        userVM.validator = new Validator(regexConfig);
        userVM.errors = ko.observable({});

        // Load user by ID
        const editId = localStorage.getItem('editUserId');
        if(editId) await userVM.loadUserById(editId);

        ko.applyBindings({ userVM });
    })();
    </script>
</body>
</html>
