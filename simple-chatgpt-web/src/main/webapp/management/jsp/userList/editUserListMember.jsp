<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User List Members</title>
	<script src="../../js/knockout-latest.js"></script>
	<script src="../../component/customize-components-3.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>
	<customize-form params="
	    formTitle: 'Edit Member',
	    formConfig: userListMemberVM.formConfig,
	    currentObject: userListMemberVM.currentObject,
	    errors: userListMemberVM.errors,
	    actions: userListMemberVM.actionGroupMap['userListMember-form-actions'],
	    invokeAction: userListMemberVM.invokeAction">
	</customize-form>

    <script type="module">
		import { UserList, UserListViewModel } from './userList.js';
		import { UserListMember, UserListMemberViewModel } from './userListMember.js';
		import configLoader from "../../js/configLoader.js";
		import Validator from "../../js/validation.js";

        (async function () {
            // ✅ Load form config for editing User List Members
            const formConfig = await configLoader.getFormConfig("editUserListMember");
			const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel for User List Members
            const userListMemberVM = new UserListMemberViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            userListMemberVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            userListMemberVM.errors = ko.observable({});

            // ✅ Load User List Member by ID from localStorage
            const editId = localStorage.getItem("editUserListMemberId");
            if (editId) {
                await userListMemberVM.loadUserListMemberById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ userListMemberVM });
        })();
    </script>
</body>
</html>
