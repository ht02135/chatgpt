<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User List Members</title>

    <!-- KnockoutJS -->
    <script src="../../../js/knockout-latest.js"></script>

    <!-- Generic Components -->
    <script src="genericComponents-2.0.js"></script>

    <!-- Page-specific JS -->
    <script type="module" src="userListMember.js"></script>

    <!-- Styles -->
    <link rel="stylesheet" href="userList.css">
</head>
<body>

<div id="userListMembersPage">

    <!-- Search Form -->
    <generic-search-form params="
        searchConfig: objectVM.searchConfig, 
        searchParams: objectVM.searchParams, 
        errors: objectVM.errors, 
        searchObjects: objectVM.searchUserListMembers
    "></generic-search-form>

    <!-- Search Actions -->
    <generic-search-actions params="
        addObject: objectVM.navigateToAddUserListMember,
        searchObjects: objectVM.searchUserListMembers,
        resetSearch: objectVM.resetSearch
    "></generic-search-actions>

    <!-- Grid -->
    <generic-grid params="
        gridConfig: objectVM.gridConfig,
        items: objectVM.members,
        sortField: objectVM.sortField,
        sortOrder: objectVM.sortOrder,
        setSort: objectVM.setSort,
        getActionsForColumn: objectVM.getActionsForColumn,
        invokeAction: objectVM.invokeAction
    "></generic-grid>

    <!-- Pagination -->
    <generic-grid-pagination params="
        page: objectVM.page,
        maxPage: objectVM.maxPage,
        prevPage: objectVM.prevPage,
        nextPage: objectVM.nextPage,
        size: objectVM.size,
        total: objectVM.total
    "></generic-grid-pagination>

</div>

<!-- Module Script to load config and bind VM -->
<script type="module">
import configLoader from "./configLoader.js";
import Validator from "./validation.js";
import { UserListMemberViewModel } from "./userListMember.js";

(async function () {
    console.log("Initializing User List Members page...");

    // Get User List ID from query param
    const userListId = new URLSearchParams(window.location.search).get('id');

    // Load configs
    const gridConfig      = await configLoader.getGridConfig("userListMembers");
    const searchConfig    = await configLoader.getFormConfig("searchUserListMember");
    const actionGroupMap  = await configLoader.getActionGroupMap();
    const formConfig      = await configLoader.getFormConfig("editUserListMember");

    // Initialize ViewModel
    const objectVM = new UserListMemberViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, form: formConfig, actionGroups: actionGroupMap },
        userListId
    );

    // Build Validator
    objectVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    objectVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ objectVM });

    // Initial load of members
    await objectVM.loadUserListMembers();
})();
</script>

</body>
</html>
