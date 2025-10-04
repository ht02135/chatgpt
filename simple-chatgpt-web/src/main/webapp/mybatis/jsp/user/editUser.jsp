<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <script src="genericComponents.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
	<!-- Use the reusable component -->
	<generic-composed-form params="vm: userVM"></generic-composed-form>

	<script type="module">
	import Validator from "./validation.js"; // correct relative path

	(async function () {
	    // ✅ Load form config
	    const formConfig = await configLoader.getFormConfig("editUser");

	    // ✅ Initialize ViewModel
	    const userVM = new UserViewModel(
	        { mode: "edit" },
	        { form: formConfig }
	    );

	    // ✅ Build Validator (loads regexConfig internally)
	    userVM.validator = await Validator.build(configLoader);

	    // ✅ Initialize observable for errors
	    userVM.errors = ko.observable({});

	    // ✅ Load user by ID from localStorage
	    const editId = localStorage.getItem("editUserId");
	    if (editId) {
	        await userVM.loadUserById(editId);
	    }

	    // ✅ Apply Knockout bindings
	    ko.applyBindings({ userVM });
	})();
	</script>

</body>
</html>
