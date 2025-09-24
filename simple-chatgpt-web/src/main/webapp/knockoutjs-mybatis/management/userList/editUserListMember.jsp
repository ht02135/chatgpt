<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User List Members</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="userListMember.js"></script>
    <script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>
    <!-- Use the reusable generic form component -->
    <generic-composed-form params="vm: userListMemberVM, formTitle: userListMemberVM.mode === 'edit' ? 'Edit User List Member' : 'Add User List Member'"></generic-composed-form>

    <script type="module">
        import configLoader from "./configLoader.js";
        import Validator from "./validation.js";

        (async function () {
            // ✅ Load form config for editing User List Members
            const formConfig = await configLoader.getFormConfig("editUserListMember");

            // ✅ Initialize ViewModel for User List Members
            const userListMemberVM = new UserListMemberViewModel(
                { mode: "edit" },
                { form: formConfig }
            );

            // ✅ Build Validator
            userListMemberVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            userListMemberVM.errors = ko.observable({});

            // ✅ Load User List Member by ID from localStorage
            const editId = localStorage.getItem("editUserListMemberId");
            if (editId) {
                await userListMemberVM.loadMemberById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ userListMemberVM });
        })();
    </script>
</body>
</html>
