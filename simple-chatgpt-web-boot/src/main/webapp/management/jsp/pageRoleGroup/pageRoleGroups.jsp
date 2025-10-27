<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Role Group Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="pageRoleGroup.css">
</head>
<body>
<div id="pageRoleGroupsPage">
    <h1>Page Role Group Management</h1>

    <!-- Search Form -->
    <customize-search-form params="
        searchConfig: PageRoleGroupVM.searchConfig,
        searchParams: PageRoleGroupVM.searchParams,
        errors: PageRoleGroupVM.errors,
        searchObjects: PageRoleGroupVM.searchPageRoleGroups,
        coreCount: 3
    "></customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions params="
        actions: PageRoleGroupVM.actionGroupMap['search-page-role-group-actions'],
        invokeAction: PageRoleGroupVM.invokeAction
    "></customize-search-actions>

    <!-- Grid -->
    <customize-grid params="
        gridConfig: PageRoleGroupVM.gridConfig,
        items: PageRoleGroupVM.pageRoleGroups,
        sortField: PageRoleGroupVM.sortField,
        sortOrder: PageRoleGroupVM.sortOrder,
        setSort: PageRoleGroupVM.setSort,
        getActionsForColumn: PageRoleGroupVM.getActionsForColumn,
        invokeAction: PageRoleGroupVM.invokeAction
    "></customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="
        page: PageRoleGroupVM.page,
        maxPage: PageRoleGroupVM.maxPage,
        prevPage: PageRoleGroupVM.prevPage,
        nextPage: PageRoleGroupVM.nextPage,
        size: PageRoleGroupVM.size,
        total: PageRoleGroupVM.total
    "></customize-grid-pagination>
</div>

<script type="module">
import { PageRoleGroup, PageRoleGroupViewModel } from './pageRoleGroup.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function() {
    console.log("Initializing Page Role Group page...");

	// Load configs
    const gridConfig = await configLoader.getGridConfig("pageRoleGroups");
    const searchConfig = await configLoader.getFormConfig("searchPageRoleGroup");
    const actionGroupMap = await configLoader.getActionGroupMap();

	// Initialize ViewModel
    window.PageRoleGroupVM = new PageRoleGroupViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

 	// Build Validator
    PageRoleGroupVM.validator = await Validator.build(configLoader);

    // Initial load of Role Groups
    PageRoleGroupVM.errors = ko.observable({});

    // Initial load of Role Groups
    ko.applyBindings({ PageRoleGroupVM });
    
    // Initial load of Page Role Groups
    await PageRoleGroupVM.loadPageRoleGroups();
})();
</script>

</body>
</html>
