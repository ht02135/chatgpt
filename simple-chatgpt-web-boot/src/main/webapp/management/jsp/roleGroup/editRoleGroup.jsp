<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Role Group</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="roleGroup.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Edit Role Group',
        formConfig: roleGroupVM.formConfig,
        currentObject: roleGroupVM.currentObject,
        errors: roleGroupVM.errors,
        actions: roleGroupVM.actionGroupMap['role-group-form-actions'],
        invokeAction: roleGroupVM.invokeAction">
    </customize-form>

    <script type="module">
        import { RoleGroup, RoleGroupViewModel } from './roleGroups.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log('Initializing Edit Role Group page...');

            // ✅ Load form config for editing Role Groups
            const formConfig = await configLoader.getFormConfig("editRoleGroup");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel for Role Groups
            const roleGroupVM = new RoleGroupViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            roleGroupVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            roleGroupVM.errors = ko.observable({});

            // ✅ Load Role Group by ID from localStorage
            const editId = localStorage.getItem("editRoleGroupId");
            if (editId) {
                await roleGroupVM.loadRoleGroupById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ roleGroupVM });
        })();
    </script>
</body>
</html>
