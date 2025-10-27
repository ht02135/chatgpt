<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User Role Group</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="userRoleGroup.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Add User Role Group',
        formConfig: userRoleGroupVM.formConfig,
        currentObject: userRoleGroupVM.currentObject,
        errors: userRoleGroupVM.errors,
        actions: userRoleGroupVM.actionGroupMap['user-role-group-form-actions'],
        invokeAction: userRoleGroupVM.invokeAction">
    </customize-form>

    <script type="module">
        import { UserRoleGroup, UserRoleGroupViewModel } from './userRoleGroup.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log('Initializing Add User Role Group page...');

            // ✅ Load form config for adding User Role Group
            const formConfig = await configLoader.getFormConfig("addUserRoleGroup");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel for User Role Groups
            const userRoleGroupVM = new UserRoleGroupViewModel(
                { mode: "add" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            userRoleGroupVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            userRoleGroupVM.errors = ko.observable({});

            // ✅ Initialize current mapping as empty
            userRoleGroupVM.currentObject(new UserRoleGroup({}, formConfig.fields));

            // ✅ Apply Knockout bindings
            ko.applyBindings({ userRoleGroupVM });
        })();
    </script>
</body>
</html>
