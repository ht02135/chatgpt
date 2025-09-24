<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User List Members</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="userListMember-2.0.js"></script>
    <script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>
    <!-- Render component only when ViewModel is ready -->
    <div data-bind="if: vmReady">
        <generic-composed-form 
            params="vm: userListMemberVM, 
                    formTitle: userListMemberVM.mode === 'edit' ? 'Edit User List Member' : 'Add User List Member'">
        </generic-composed-form>
    </div>

    <script type="module">
        import configLoader from "./configLoader.js";
        import Validator from "./validation.js";

        (async function () {
            // ✅ Observable to indicate when ViewModel is ready
            const vmReady = ko.observable(false);

            // ✅ Load form config
            const formConfig = await configLoader.getFormConfig("editUserListMember");

            // ✅ Initialize ViewModel
            const userListMemberVM = new UserListMemberViewModel(
                { mode: "edit" },
                { form: formConfig }
            );

            // ✅ Build validator
            userListMemberVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            userListMemberVM.errors = ko.observable({});

            // ✅ Load member if editing
            const editId = localStorage.getItem("editUserListMemberId");
            if (editId) {
                await userListMemberVM.loadUserListMemberById(editId);
            }

            // ✅ Apply Knockout bindings including vmReady
            ko.applyBindings({ userListMemberVM, vmReady });

            // ✅ Mark VM ready so the component renders
            vmReady(true);
        })();
    </script>
</body>
</html>
