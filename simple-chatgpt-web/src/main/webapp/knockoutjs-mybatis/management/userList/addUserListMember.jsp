<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User List Member</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>
	<generic-edit-form params="
	  saveObject: userListMemberVM.saveObject,
	  formConfig: userListMemberVM.formConfig,
	  currentObject: userListMemberVM.currentObject,
	  errors: userListMemberVM.errors,
	  navigateToObjects: userListMemberVM.navigateToObjects,
	  formTitle: 'Add Member'">
	</generic-edit-form>

    <script type="module">
		import { UserListViewModel } from './userList.js';
		import { UserListMemberViewModel } from './userListMember.js';
        import configLoader from "./configLoader.js";
        import Validator from "./validation.js";

        (async function () {
            // ✅ Load form config for User List Members
            const formConfig = await configLoader.getFormConfig("addUserListMember");

            // ✅ Initialize ViewModel for User List Members
            const userListMemberVM = new UserListMemberViewModel(
                { mode: "add" },
                { form: formConfig }
            );

            // ✅ Build Validator
            userListMemberVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            userListMemberVM.errors = ko.observable({});

            // ✅ Initialize the current member (empty object)
            userListMemberVM.currentObject(new UserListMember({}, formConfig.fields));

            // ✅ Apply Knockout bindings
            ko.applyBindings({ userListMemberVM });
        })();
    </script>

</body>
</html>
