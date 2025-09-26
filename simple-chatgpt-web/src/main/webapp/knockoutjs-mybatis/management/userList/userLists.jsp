<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Lists</title>
	<script src="../../../js/knockout-latest.js"></script>
	<script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>

<div id="userListsPage">

    <!-- Search Form -->
    <generic-search-form params="
        searchConfig: UserListVM.searchConfig, 
        searchParams: UserListVM.searchParams, 
        errors: UserListVM.errors, 
        searchObjects: UserListVM.searchUserLists,
		coreCount: 3
    "></generic-search-form>

    <!-- Search Actions -->
	<customize-search-actions params="
	    actions: UserListVM.actionGroupMap['search-userList-actions'],
	    invokeAction: UserListVM.invokeAction
	"></customize-search-actions>

    <!-- Grid -->
    <generic-grid params="
        gridConfig: UserListVM.gridConfig,
        items: UserListVM.userLists,
        sortField: UserListVM.sortField,
        sortOrder: UserListVM.sortOrder,
        setSort: UserListVM.setSort,
        getActionsForColumn: UserListVM.getActionsForColumn,
        invokeAction: UserListVM.invokeAction
    "></generic-grid>

    <!-- Pagination -->
    <generic-grid-pagination params="
        page: UserListVM.page,
        maxPage: UserListVM.maxPage,
        prevPage: UserListVM.prevPage,
        nextPage: UserListVM.nextPage,
        size: UserListVM.size,
        total: UserListVM.total
    "></generic-grid-pagination>

</div>

<!-- Initialization Script -->
<script type="module">
import { UserListViewModel } from './userList.js';
import { UserListMemberViewModel } from './userListMember.js';
import configLoader from "./configLoader.js";
import Validator from "./validation.js";

(async function () {
    console.log("Initializing User Lists page...");

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("userLists");
    const searchConfig   = await configLoader.getFormConfig("searchUserList");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel (imported from userList.js)
    window.UserListVM = new UserListViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Build Validator
    UserListVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    UserListVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ UserListVM });

    // Initial load of User Lists
    await UserListVM.loadUserLists();
})();
</script>

</body>
</html>
