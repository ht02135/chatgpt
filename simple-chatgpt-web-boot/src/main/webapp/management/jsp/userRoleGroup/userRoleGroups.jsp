<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Role Group Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="userRoleGroup.css">
</head>
<body>
<div id="userRoleGroupsPage">
    <h1>User Role Group Management</h1>

    <!-- Search Form -->
    <customize-search-form params="
        searchConfig: UserRoleGroupVM.searchConfig,
        searchParams: UserRoleGroupVM.searchParams,
        errors: UserRoleGroupVM.errors,
        searchObjects: UserRoleGroupVM.searchUserRoleGroups,
        coreCount: 3
    "></customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions params="
        actions: UserRoleGroupVM.actionGroupMap['search-user-role-group-actions'],
        invokeAction: UserRoleGroupVM.invokeAction
    "></customize-search-actions>

    <!-- Grid -->
    <customize-grid params="
        gridConfig: UserRoleGroupVM.gridConfig,
        items: UserRoleGroupVM.userRoleGroups,
        sortField: UserRoleGroupVM.sortField,
        sortOrder: UserRoleGroupVM.sortOrder,
        setSort: UserRoleGroupVM.setSort,
        getActionsForColumn: UserRoleGroupVM.getActionsForColumn,
        invokeAction: UserRoleGroupVM.invokeAction
    "></customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="
        page: UserRoleGroupVM.page,
        maxPage: UserRoleGroupVM.maxPage,
        prevPage: UserRoleGroupVM.prevPage,
        nextPage: UserRoleGroupVM.nextPage,
        size: UserRoleGroupVM.size,
        total: UserRoleGroupVM.total
    "></customize-grid-pagination>
</div>

<script type="module">
import { UserRoleGroup, UserRoleGroupViewModel } from './userRoleGroup.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing User Role Group page...");

    const gridConfig     = await configLoader.getGridConfig("userRoleGroups");
    const searchConfig   = await configLoader.getFormConfig("searchUserRoleGroup");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel
    window.UserRoleGroupVM = new UserRoleGroupViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Initialize ViewModel
    UserRoleGroupVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    UserRoleGroupVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ UserRoleGroupVM });

	// Initial load of User Role Groups
    await UserRoleGroupVM.loadUserRoleGroups();
})();
</script>
</body>
</html>
