<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
	<head>
	    <meta charset="UTF-8">
	    <title>Job Requests Management</title>

	    <!-- Knockout first -->
	    <script src="../../js/knockout-latest.js"></script>

	    <!-- Client-side constants before components -->
	    <script src="<%= request.getContextPath() %>/management/js/constants.js"></script>

		<!-- Server-side constants (optional, can stay here if needed) -->
		<%@ include file="/management/include/constants.jspf" %>
		
	    <!-- Component scripts -->
	    <script src="../../component/customize-components-3.0.js"></script>

	    <link rel="stylesheet" href="jobRequests.css">
	</head>

<body>
<div id="jobRequestsPage">
    <h1>Job Requests Management</h1>

    <!-- ========================== -->
    <!-- Search Form -->
    <!-- ========================== -->
    <customize-search-form 
        params="searchConfig: JobRequestVM.searchConfig, 
                searchParams: JobRequestVM.searchParams, 
                errors: JobRequestVM.errors, 
                searchObjects: JobRequestVM.searchObjects,
                coreCount: 6">
    </customize-search-form>

    <!-- ========================== -->
    <!-- Search Actions -->
    <!-- ========================== -->
    <customize-search-actions
        params="actions: JobRequestVM.actionGroupMap['search-job-request-actions'],
                invokeAction: JobRequestVM.invokeAction">
    </customize-search-actions>

    <!-- ========================== -->
    <!-- Grid Section -->
    <!-- ========================== -->
    <customize-grid 
        params="gridConfig: JobRequestVM.gridConfig, 
                items: JobRequestVM.objects, 
                sortField: JobRequestVM.sortField, 
                sortOrder: JobRequestVM.sortOrder, 
                setSort: JobRequestVM.setSort, 
                getActionsForColumn: JobRequestVM.getActionsForColumn, 
                invokeAction: JobRequestVM.invokeAction">
    </customize-grid>

    <!-- ========================== -->
    <!-- Pagination -->
    <!-- ========================== -->
    <customize-grid-pagination 
        params="page: JobRequestVM.page, 
                maxPage: JobRequestVM.maxPage, 
                prevPage: JobRequestVM.prevPage, 
                nextPage: JobRequestVM.nextPage, 
                size: JobRequestVM.size, 
                total: JobRequestVM.total">
    </customize-grid-pagination>
</div>

<!-- ==============================
     CLIENT-SIDE SCRIPT BLOCK
     ============================== -->
<script type="module">
import { JobRequest, JobRequestViewModel } from './jobRequest.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

// ===== Check JWT token =====
const jwtToken = localStorage.getItem('jwtToken');
if (!jwtToken) {
    console.debug("jobRequests.jsp -> No token found in localStorage, redirecting to login");
    window.location.href = CONTEXT_PATH + "/login.jsp";
} else {
    console.debug("jobRequests.jsp -> JWT token found:", jwtToken);

    (async function () {
        console.log("Initializing JobRequests page...");

        // Load configurations
        const gridConfig     = await configLoader.getGridConfig("jobRequests");
        const searchConfig   = await configLoader.getFormConfig("searchJobRequest");
        const actionGroupMap = await configLoader.getActionGroupMap();

        // Initialize ViewModel
        window.JobRequestVM = new JobRequestViewModel(
            { mode: "list" },
            { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
        );

        // Build Validator
        JobRequestVM.validator = await Validator.build(configLoader);

        // Initialize observable for errors
        JobRequestVM.errors = ko.observable({});

        // Apply Knockout bindings
        ko.applyBindings({ JobRequestVM });

        // Initial load of job requests
        await JobRequestVM.loadJobRequests();
    })();
}
</script>

</body>
</html>
