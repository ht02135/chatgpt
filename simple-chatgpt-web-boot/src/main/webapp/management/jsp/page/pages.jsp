<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
    <!-- Server-side constants -->
    <%@ include file="/management/include/constants.jspf" %>
    <!-- Client-side constants -->
    <script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="page.css">
</head>
<body>
<div id="pagesPage">
    <h1>Page Management</h1>

    <!-- Search Section -->
    <customize-search-form 
        params="searchConfig: PageVM.searchConfig, 
                searchParams: PageVM.searchParams, 
                errors: PageVM.errors, 
                searchObjects: PageVM.searchObjects,
                coreCount: 5">
    </customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions
        params="actions: PageVM.actionGroupMap['search-page-actions'],
                invokeAction: PageVM.invokeAction">
    </customize-search-actions>

    <!-- Grid Section -->
    <customize-grid 
        params="gridConfig: PageVM.gridConfig, 
                items: PageVM.objects, 
                sortField: PageVM.sortField, 
                sortOrder: PageVM.sortOrder, 
                setSort: PageVM.setSort, 
                getActionsForColumn: PageVM.getActionsForColumn, 
                invokeAction: PageVM.invokeAction">
    </customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination 
        params="page: PageVM.page, 
                maxPage: PageVM.maxPage, 
                prevPage: PageVM.prevPage, 
                nextPage: PageVM.nextPage, 
                size: PageVM.size, 
                total: PageVM.total">
    </customize-grid-pagination>
</div>

<!-- ==============================
     CLIENT-SIDE: Single script block for token + initialization
     ============================== -->
<script type="module">
import { Page, PageViewModel } from './page.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

// ===== Check JWT token from localStorage =====
const jwtToken = localStorage.getItem('jwtToken');
if (!jwtToken) {
    console.debug("pages.jsp -> No token found in localStorage, redirecting to login");
    window.location.href = CONTEXT_PATH + "/login.jsp";
} else {
    console.debug("pages.jsp -> JWT token found:", jwtToken);

    (async function () {
        console.log("Initializing Pages page...");

        // Load configs
        const gridConfig     = await configLoader.getGridConfig("pages");
        const searchConfig   = await configLoader.getFormConfig("searchPage");
        const actionGroupMap = await configLoader.getActionGroupMap();

        // Initialize ViewModel
        window.PageVM = new PageViewModel(
            { mode: "list" },
            { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
        );

        // Build Validator
        PageVM.validator = await Validator.build(configLoader);

        // Initialize observable for errors
        PageVM.errors = ko.observable({});

        // Apply Knockout bindings
        ko.applyBindings({ PageVM });

        // Initial load of pages
        await PageVM.loadPages();
    })();
}
</script>

</body>
</html>
