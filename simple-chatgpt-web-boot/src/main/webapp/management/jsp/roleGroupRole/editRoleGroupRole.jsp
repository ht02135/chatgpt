<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Role Group Role</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="roleGroupRole.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Edit Role Group Role',
        formConfig: roleGroupRoleVM.formConfig,
        currentObject: roleGroupRoleVM.currentObject,
        errors: roleGroupRoleVM.errors,
        actions: roleGroupRoleVM.actionGroupMap['role-group-role-form-actions'],
        invokeAction: roleGroupRoleVM.invokeAction">
    </customize-form>

    <script type="module">
        import { RoleGroupRole, RoleGroupRoleViewModel } from './roleGroupRoles.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log('Initializing Edit Role Group Role page...');

            // ✅ Load form config for editing Role Group Roles
            const formConfig = await configLoader.getFormConfig("editRoleGroupRole");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel
            const roleGroupRoleVM = new RoleGroupRoleViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            roleGroupRoleVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            roleGroupRoleVM.errors = ko.observable({});

            // ✅ Load Role Group Role by ID from localStorage
            const editId = localStorage.getItem("editRoleGroupRoleId");
            if (editId) {
                await roleGroupRoleVM.loadRoleGroupRoleById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ roleGroupRoleVM });
        })();
    </script>
</body>
</html>
