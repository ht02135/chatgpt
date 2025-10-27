<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Role Group Role Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="roleGroupRole.css">
</head>
<body>
<div id="roleGroupRolesPage">
    <h1>Role Group Role Management</h1>

    <!-- Search Form -->
    <customize-search-form params="
        searchConfig: RoleGroupRoleVM.searchConfig,
        searchParams: RoleGroupRoleVM.searchParams,
        errors: RoleGroupRoleVM.errors,
        searchObjects: RoleGroupRoleVM.searchRoleGroupRoles,
        coreCount: 3
    "></customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions params="
        actions: RoleGroupRoleVM.actionGroupMap['search-role-group-role-actions'],
        invokeAction: RoleGroupRoleVM.invokeAction
    "></customize-search-actions>

    <!-- Grid -->
    <customize-grid params="
        gridConfig: RoleGroupRoleVM.gridConfig,
        items: RoleGroupRoleVM.roleGroupRoles,
        sortField: RoleGroupRoleVM.sortField,
        sortOrder: RoleGroupRoleVM.sortOrder,
        setSort: RoleGroupRoleVM.setSort,
        getActionsForColumn: RoleGroupRoleVM.getActionsForColumn,
        invokeAction: RoleGroupRoleVM.invokeAction
    "></customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="
        page: RoleGroupRoleVM.page,
        maxPage: RoleGroupRoleVM.maxPage,
        prevPage: RoleGroupRoleVM.prevPage,
        nextPage: RoleGroupRoleVM.nextPage,
        size: RoleGroupRoleVM.size,
        total: RoleGroupRoleVM.total
    "></customize-grid-pagination>
</div>

<!-- Initialization Script -->
<script type="module">
import { RoleGroupRole, RoleGroupRoleViewModel } from './roleGroupRoles.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing RoleGroupRoles page...");

    const gridConfig = await configLoader.getGridConfig("roleGroupRoles");
    const searchConfig = await configLoader.getFormConfig("searchRoleGroupRole");
    const actionGroupMap = await configLoader.getActionGroupMap();

    window.RoleGroupRoleVM = new RoleGroupRoleViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    RoleGroupRoleVM.validator = await Validator.build(configLoader);
    RoleGroupRoleVM.errors = ko.observable({});

    ko.applyBindings({ RoleGroupRoleVM });
    await RoleGroupRoleVM.loadRoleGroupRoles();
})();
</script>
</body>
</html>
