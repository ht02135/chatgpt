<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reset User Password</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<!-- Server-side constants -->
	<%@ include file="/management/include/constants.jspf" %>
	<!-- Client-side constants -->
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
	<customize-form params="
	    formTitle: 'Reset User Password',
	    formConfig: userVM.formConfig,
	    currentObject: userVM.currentObject,
	    errors: userVM.errors,
	    actions: userVM.actionGroupMap['user-form-actions'],
	    invokeAction: userVM.invokeAction">
	</customize-form>

    <script type="module">
        import { User, UserViewModel } from './user.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            // ✅ Load form config
            const formConfig = await configLoader.getFormConfig("resetUserPassword");
			const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel
            const userVM = new UserViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
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
