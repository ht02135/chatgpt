<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Lists</title>
	<script src="../../js/knockout-latest.js"></script>
	<script src="../../component/customize-components-3.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>
<div id="userListsPage">
    <h1>User List Management</h1>

    <!-- Search Form -->
    <customize-search-form params="
        searchConfig: UserListVM.searchConfig, 
        searchParams: UserListVM.searchParams, 
        errors: UserListVM.errors, 
        searchObjects: UserListVM.searchUserLists,
		coreCount: 3
    "></customize-search-form>

    <!-- Search Actions -->
	<customize-search-actions params="
	    actions: UserListVM.actionGroupMap['search-userList-actions'],
	    invokeAction: UserListVM.invokeAction
	"></customize-search-actions>

    <!-- Grid -->
    <customize-grid params="
        gridConfig: UserListVM.gridConfig,
        items: UserListVM.userLists,
        sortField: UserListVM.sortField,
        sortOrder: UserListVM.sortOrder,
        setSort: UserListVM.setSort,
        getActionsForColumn: UserListVM.getActionsForColumn,
        invokeAction: UserListVM.invokeAction
    "></customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="
        page: UserListVM.page,
        maxPage: UserListVM.maxPage,
        prevPage: UserListVM.prevPage,
        nextPage: UserListVM.nextPage,
        size: UserListVM.size,
        total: UserListVM.total
    "></customize-grid-pagination>

</div>

<!-- Initialization Script -->
<script type="module">
import { UserList, UserListViewModel } from './userList.js';
import { UserListMember, UserListMemberViewModel } from './userListMember.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

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
