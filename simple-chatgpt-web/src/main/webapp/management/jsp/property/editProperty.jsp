<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Property</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
    <link rel="stylesheet" href="property.css">
</head>
<body>
	<customize-form params="
	    formTitle: 'Edit Property',
	    formConfig: propertyVM.formConfig,
	    currentObject: propertyVM.currentObject,
	    errors: propertyVM.errors,
	    actions: propertyVM.actionGroupMap['property-form-actions'],
	    invokeAction: propertyVM.invokeAction">
	</customize-form>


    <script type="module">
        import { Property, PropertyViewModel } from './property.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            // ✅ Load form config
            const formConfig = await configLoader.getFormConfig("editProperty");
			const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel
            const propertyVM = new PropertyViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            propertyVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            propertyVM.errors = ko.observable({});

            // ✅ Load property by ID from localStorage
            const editId = localStorage.getItem("editPropertyId");
            if (editId) {
                await propertyVM.loadPropertyById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ propertyVM });
        })();
    </script>
</body>
</html>
