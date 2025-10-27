<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Role Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="role.css">
</head>
<body>
<div id="rolesPage">
    <h1>Role Management</h1>

    <!-- Search Form -->
    <customize-search-form params="
        searchConfig: RoleVM.searchConfig, 
        searchParams: RoleVM.searchParams, 
        errors: RoleVM.errors, 
        searchObjects: RoleVM.searchRoles,
        coreCount: 3
    "></customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions params="
        actions: RoleVM.actionGroupMap['search-role-actions'],
        invokeAction: RoleVM.invokeAction
    "></customize-search-actions>

    <!-- Grid -->
    <customize-grid params="
        gridConfig: RoleVM.gridConfig,
        items: RoleVM.roles,
        sortField: RoleVM.sortField,
        sortOrder: RoleVM.sortOrder,
        setSort: RoleVM.setSort,
        getActionsForColumn: RoleVM.getActionsForColumn,
        invokeAction: RoleVM.invokeAction
    "></customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="
        page: RoleVM.page,
        maxPage: RoleVM.maxPage,
        prevPage: RoleVM.prevPage,
        nextPage: RoleVM.nextPage,
        size: RoleVM.size,
        total: RoleVM.total
    "></customize-grid-pagination>
</div>

<!-- Initialization Script -->
<script type="module">
import { Role, RoleViewModel } from './role.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing Roles page...");

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("roles");
    const searchConfig   = await configLoader.getFormConfig("searchRole");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel
    window.RoleVM = new RoleViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Build Validator
    RoleVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    RoleVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ RoleVM });

    // Initial load of Roles
    await RoleVM.loadRoles();
})();
</script>

</body>
</html>
