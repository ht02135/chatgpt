<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Page Role Group</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="pageRoleGroup.css">
</head>
<body>
    <!-- ========================= -->
    <!-- Add Page Role Group Form -->
    <!-- ========================= -->
    <customize-form params="
        formTitle: 'Add Page Role Group',
        formConfig: PageRoleGroupVM.formConfig,
        currentObject: PageRoleGroupVM.currentObject,
        errors: PageRoleGroupVM.errors,
        actions: PageRoleGroupVM.actionGroupMap['page-role-group-form-actions'],
        invokeAction: PageRoleGroupVM.invokeAction">
    </customize-form>

    <script type="module">
        import { PageRoleGroup, PageRoleGroupViewModel } from './pageRoleGroup.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log('Initializing Add Page Role Group page...');

            // ✅ Load form config and action group map
            const formConfig = await configLoader.getFormConfig("addPageRoleGroup");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel
            window.PageRoleGroupVM = new PageRoleGroupViewModel(
                { mode: "add" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build validator
            PageRoleGroupVM.validator = await Validator.build(configLoader);

            // ✅ Initialize errors observable
            PageRoleGroupVM.errors = ko.observable({});

            // ✅ Initialize current PageRoleGroup as empty
            PageRoleGroupVM.currentObject(new PageRoleGroup({}, formConfig.fields));

            // ✅ Apply Knockout bindings
            ko.applyBindings({ PageRoleGroupVM });
        })();
    </script>
</body>
</html>
