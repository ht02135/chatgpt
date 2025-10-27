<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
	<customize-form params="
	    formTitle: 'Add User',
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
            const formConfig = await configLoader.getFormConfig("addUser");
			const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel
            const userVM = new UserViewModel(
                { mode: "add" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            userVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            userVM.errors = ko.observable({});

            // ✅ Initialize currentObject (empty User)
            userVM.currentObject(new User({}, formConfig.fields));

            // ✅ Apply Knockout bindings
            ko.applyBindings({ userVM });
        })();
    </script>
</body>
</html>
