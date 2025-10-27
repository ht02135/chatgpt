<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role Group Role</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="roleGroupRole.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Add Role Group Role',
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
            console.log('Initializing Add Role Group Role page...');

            // ✅ Load form config for adding Role Group Roles
            const formConfig = await configLoader.getFormConfig("addRoleGroupRole");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel for Role Group Roles
            const roleGroupRoleVM = new RoleGroupRoleViewModel(
                { mode: "add" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            roleGroupRoleVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            roleGroupRoleVM.errors = ko.observable({});

            // ✅ Initialize empty RoleGroupRole object
            roleGroupRoleVM.currentObject(new RoleGroupRole({}, formConfig.fields));

            // ✅ Apply Knockout bindings
            ko.applyBindings({ roleGroupRoleVM });
        })();
    </script>
</body>
</html>
