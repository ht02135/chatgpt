<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Page</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
    <!-- Server-side constants -->
    <%@ include file="/management/include/constants.jspf" %>
    <!-- Client-side constants -->
    <script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="page.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Add Page',
        formConfig: pageVM.formConfig,
        currentObject: pageVM.currentObject,
        errors: pageVM.errors,
        actions: pageVM.actionGroupMap['page-form-actions'],
        invokeAction: pageVM.invokeAction">
    </customize-form>

    <script type="module">
        import { Page, PageViewModel } from './page.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log("Initializing Add Page form...");

            // Load form config
            const formConfig = await configLoader.getFormConfig("addPage");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // Initialize ViewModel
            const pageVM = new PageViewModel(
                { mode: "add" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // Build Validator
            pageVM.validator = await Validator.build(configLoader);

            // Initialize observable for errors
            pageVM.errors = ko.observable({});

            // Initialize currentObject (empty Page)
            pageVM.currentObject(new Page({}, formConfig.fields));

            // Apply Knockout bindings
            ko.applyBindings({ pageVM });
        })();
    </script>
</body>
</html>
