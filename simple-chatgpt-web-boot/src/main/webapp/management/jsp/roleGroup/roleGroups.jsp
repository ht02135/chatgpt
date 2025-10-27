<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Role Group Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="roleGroup.css">
</head>
<body>
<div id="roleGroupsPage">
    <h1>Role Group Management</h1>

    <!-- Search Form -->
    <customize-search-form params="
        searchConfig: RoleGroupVM.searchConfig, 
        searchParams: RoleGroupVM.searchParams, 
        errors: RoleGroupVM.errors, 
        searchObjects: RoleGroupVM.searchRoleGroups,
        coreCount: 3
    "></customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions params="
        actions: RoleGroupVM.actionGroupMap['search-role-group-actions'],
        invokeAction: RoleGroupVM.invokeAction
    "></customize-search-actions>

    <!-- Grid -->
    <customize-grid params="
        gridConfig: RoleGroupVM.gridConfig,
        items: RoleGroupVM.roleGroups,
        sortField: RoleGroupVM.sortField,
        sortOrder: RoleGroupVM.sortOrder,
        setSort: RoleGroupVM.setSort,
        getActionsForColumn: RoleGroupVM.getActionsForColumn,
        invokeAction: RoleGroupVM.invokeAction
    "></customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="
        page: RoleGroupVM.page,
        maxPage: RoleGroupVM.maxPage,
        prevPage: RoleGroupVM.prevPage,
        nextPage: RoleGroupVM.nextPage,
        size: RoleGroupVM.size,
        total: RoleGroupVM.total
    "></customize-grid-pagination>
</div>

<!-- Initialization Script -->
<script type="module">
import { RoleGroup, RoleGroupViewModel } from './roleGroups.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing Role Groups page...");

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("roleGroups");
    const searchConfig   = await configLoader.getFormConfig("searchRoleGroup");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel
    window.RoleGroupVM = new RoleGroupViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Build Validator
    RoleGroupVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    RoleGroupVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ RoleGroupVM });

    // Initial load of Role Groups
    await RoleGroupVM.loadRoleGroups();
})();
</script>

</body>
</html>
