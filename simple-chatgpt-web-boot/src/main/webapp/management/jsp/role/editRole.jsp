<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Role</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="role.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Edit Role',
        formConfig: roleVM.formConfig,
        currentObject: roleVM.currentObject,
        errors: roleVM.errors,
        actions: roleVM.actionGroupMap['role-form-actions'],
        invokeAction: roleVM.invokeAction">
    </customize-form>

    <script type="module">
        import { Role, RoleViewModel } from './role.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log('Initializing Edit Role page...');

            // ✅ Load form config for editing Roles
            const formConfig = await configLoader.getFormConfig("editRole");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel for Roles
            const roleVM = new RoleViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            roleVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            roleVM.errors = ko.observable({});

            // ✅ Load Role by ID from localStorage
            const editId = localStorage.getItem("editRoleId");
            if (editId) {
                await roleVM.loadRoleById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ roleVM });
        })();
    </script>
</body>
</html>
