<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Page Role Group</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="pageRoleGroup.css">
</head>
<body>
    <!-- ========================= -->
    <!-- Edit Page Role Group Form -->
    <!-- ========================= -->
    <customize-form params="
        formTitle: 'Edit Page Role Group',
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
            console.log('Initializing Edit Page Role Group page...');

            // ✅ Load form config for editing
            const formConfig = await configLoader.getFormConfig("editPageRoleGroup");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel in edit mode
            window.PageRoleGroupVM = new PageRoleGroupViewModel(
                { mode: "edit" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            PageRoleGroupVM.validator = await Validator.build(configLoader);

            // ✅ Initialize errors observable
            PageRoleGroupVM.errors = ko.observable({});

            // ✅ Load Page Role Group by ID from localStorage
            const editId = localStorage.getItem("editPageRoleGroupId");
            if (editId) {
                await PageRoleGroupVM.loadPageRoleGroupById(editId);
            }

            // ✅ Apply Knockout bindings
            ko.applyBindings({ PageRoleGroupVM });
        })();
    </script>
</body>
</html>
