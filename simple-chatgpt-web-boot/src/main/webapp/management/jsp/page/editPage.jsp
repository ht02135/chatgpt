<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Page</title>
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
        formTitle: 'Edit Page',
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
            console.log("Initializing Edit Page form...");

            // Load form config
            const formConfig = await configLoader.getFormConfig("editPage");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // Initialize ViewModel
            const pageVM = new PageViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // Build Validator
            pageVM.validator = await Validator.build(configLoader);

            // Initialize observable for errors
            pageVM.errors = ko.observable({});

            // Load page by ID from localStorage
            const editId = localStorage.getItem("editPageId");
            if (editId) {
                await pageVM.loadPageById(editId);
            }

            // Apply Knockout bindings
            ko.applyBindings({ pageVM });
        })();
    </script>
</body>
</html>
