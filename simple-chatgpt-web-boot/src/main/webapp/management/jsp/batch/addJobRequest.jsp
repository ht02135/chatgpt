<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Job Request</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>

    <!-- Server-side constants -->
    <%@ include file="/management/include/constants.jspf" %>
    <!-- Client-side constants -->
    <script src="<%= request.getContextPath() %>/management/js/constants.js"></script>

    <link rel="stylesheet" href="jobRequests.css">
</head>
<body>
    <customize-form params="
        formTitle: 'Add Job Request',
        formConfig: jobRequestVM.formConfig,
        currentObject: jobRequestVM.currentObject,
        errors: jobRequestVM.errors,
        actions: jobRequestVM.actionGroupMap['job-request-form-actions'],
        invokeAction: jobRequestVM.invokeAction">
    </customize-form>

    <script type="module">
        import { JobRequest, JobRequestViewModel } from './jobRequest.js';
        import configLoader from "../../js/configLoader.js";
        import Validator from "../../js/validation.js";

        (async function () {
            console.log('Initializing Add JobRequest page...');

            // ✅ Load form configuration and action groups
            const formConfig = await configLoader.getFormConfig("addJobRequest");
            const actionGroupMap = await configLoader.getActionGroupMap();

            // ✅ Initialize ViewModel
            const jobRequestVM = new JobRequestViewModel(
                { mode: "add" },
                { form: formConfig, actionGroups: actionGroupMap }
            );

            // ✅ Build Validator
            jobRequestVM.validator = await Validator.build(configLoader);

            // ✅ Initialize observable for errors
            jobRequestVM.errors = ko.observable({});

            // ✅ Initialize currentObject (empty JobRequest)
            jobRequestVM.currentObject(new JobRequest({}, formConfig.fields));

            // ✅ Apply Knockout bindings
            ko.applyBindings({ jobRequestVM });
        })();
    </script>
</body>
</html>
