<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Property</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="property.js"></script>
    <script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="property.css">
</head>
<body>
    <!-- Use the reusable generic form -->
	<generic-composed-form params="vm: propertyVM, formTitle: propertyVM.mode === 'edit' ? 'Edit Property' : 'Add Property'"></generic-composed-form>

	<script type="module">
	import configLoader from "./configLoader.js";
	import Validator from "./validation.js";

	(async function () {
	    // ✅ Load form config
	    const formConfig = await configLoader.getFormConfig("editProperty");

	    // ✅ Load validator groups map
	    const validatorGroupsMap = await configLoader.getValidatorGroupMap();

	    console.log("editProperty.jsp ##########");
	    console.log("editProperty.jsp -> validatorGroupsMap=", validatorGroupsMap);
	    console.log("editProperty.jsp -> validatorGroupsMap JSON=", JSON.stringify(validatorGroupsMap, null, 2));
	    console.log("editProperty.jsp ##########");

	    // ✅ Initialize ViewModel with form config (no validatorGroups needed)
	    const propertyVM = new PropertyViewModel(
	        { mode: "edit" },
	        { form: formConfig }
	    );

	    // ✅ Build Validator (merges regexConfig + validatorRegexConfig internally)
	    propertyVM.validator = await Validator.build(configLoader, validatorGroupsMap);

	    // ✅ Initialize observable for errors
	    propertyVM.errors = ko.observable({});

	    // ✅ Load property by ID (from localStorage)
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
