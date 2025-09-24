<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User List Members</title>
	<script src="../../../js/knockout-latest.js"></script>
	<script src="userListMember.js"></script>
	<script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>

<div id="userListMembersPage">

    <!-- Search Form -->
    <generic-search-form params="
        searchConfig: UserListMemberVM.searchConfig, 
        searchParams: UserListMemberVM.searchParams, 
        errors: UserListMemberVM.errors, 
        searchObjects: UserListMemberVM.searchUserListMembers,
		coreCount: 5
    "></generic-search-form>

    <!-- Search Actions -->
    <generic-search-actions params="
        addObject: UserListMemberVM.addUserListMember,
        searchObjects: UserListMemberVM.searchUserListMembers,
        resetSearch: UserListMemberVM.resetSearch
    "></generic-search-actions>

    <!-- Grid -->
    <generic-grid params="
        gridConfig: UserListMemberVM.gridConfig,
        items: UserListMemberVM.members,
        sortField: UserListMemberVM.sortField,
        sortOrder: UserListMemberVM.sortOrder,
        setSort: UserListMemberVM.setSort,
        getActionsForColumn: UserListMemberVM.getActionsForColumn,
        invokeAction: UserListMemberVM.invokeAction
    "></generic-grid>

    <!-- Pagination -->
    <generic-grid-pagination params="
        page: UserListMemberVM.page,
        maxPage: UserListMemberVM.maxPage,
        prevPage: UserListMemberVM.prevPage,
        nextPage: UserListMemberVM.nextPage,
        size: UserListMemberVM.size,
        total: UserListMemberVM.total
    "></generic-grid-pagination>

</div>

<!-- Initialization Script -->
<script type="module">
import configLoader from "./configLoader.js";
import Validator from "./validation.js";

(async function () {
    console.log("Initializing User List Members page...");

    // ✅ Get User List ID from localStorage (editUserListId)
    const editUserListId = localStorage.getItem("editUserListId");
    console.log("editUserList.jsp -> using editUserListId from localStorage:", editUserListId);

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("userListMembers");
    const searchConfig   = await configLoader.getFormConfig("searchUserListMember");
    const actionGroupMap = await configLoader.getActionGroupMap();
    const formConfig     = await configLoader.getFormConfig("editUserListMember");

    // Initialize ViewModel (from userListMember.js, global function)
    const UserListMemberVM = new UserListMemberViewModel(
        { mode: "list", userListId: editUserListId },
        { grid: gridConfig, search: searchConfig, form: formConfig, actionGroups: actionGroupMap },
    );

    // Build Validator
    UserListMemberVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    UserListMemberVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ UserListMemberVM });

    // Initial load of members
    await UserListMemberVM.loadUserListMembers();
})();
</script>

</body>
</html>
