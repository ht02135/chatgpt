<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User List Members</title>
    <!-- plain scripts -->
    <script src="../../../js/knockout-latest.js"></script>
    <script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>

<div id="userListMembersPage">
    <!-- Edit User List -->
    <generic-edit-form params="
      saveObject: userListVM.saveObject,
      formConfig: userListVM.formConfig,
      currentObject: userListVM.currentObject,
      errors: userListVM.errors,
      navigateToObjects: userListVM.navigateToObjects,
      formTitle: 'Edit List'">
    </generic-edit-form>

    <!-- Search Form -->
    <generic-search-form params="
        searchConfig: userListMemberVM.searchConfig, 
        searchParams: userListMemberVM.searchParams, 
        errors: userListMemberVM.errors, 
        searchObjects: userListMemberVM.searchUserListMembers,
        coreCount: 5
    "></generic-search-form>

    <!-- Search Actions -->
    <generic-search-actions params="
        addObject: userListMemberVM.addUserListMember,
        searchObjects: userListMemberVM.searchUserListMembers,
        resetSearch: userListMemberVM.resetSearch
    "></generic-search-actions>

    <!-- Grid -->
    <generic-grid params="
        gridConfig: userListMemberVM.gridConfig,
        items: userListMemberVM.members,
        sortField: userListMemberVM.sortField,
        sortOrder: userListMemberVM.sortOrder,
        setSort: userListMemberVM.setSort,
        getActionsForColumn: userListMemberVM.getActionsForColumn,
        invokeAction: userListMemberVM.invokeAction
    "></generic-grid>

    <!-- Pagination -->
    <generic-grid-pagination params="
        page: userListMemberVM.page,
        maxPage: userListMemberVM.maxPage,
        prevPage: userListMemberVM.prevPage,
        nextPage: userListMemberVM.nextPage,
        size: userListMemberVM.size,
        total: userListMemberVM.total
    "></generic-grid-pagination>
</div>

<!-- Initialization Script -->
<script type="module">
import { UserList, UserListViewModel } from './userList.js';
import { UserListMember, UserListMemberViewModel } from './userListMember.js';
import configLoader from "./configLoader.js";
import Validator from "./validation.js";

(async function () {
    console.log("Initializing User List Members page...");

    const editUserListId = localStorage.getItem("editUserListId");
    console.log("editUserList.jsp -> using editUserListId:", editUserListId);

    // separate configs for each VM
    const gridConfig                   = await configLoader.getGridConfig("userListMembers");
    const searchConfig                 = await configLoader.getFormConfig("searchUserListMember");
    const actionGroupMap               = await configLoader.getActionGroupMap();
    const editUserListFormConfig       = await configLoader.getFormConfig("editUserList");
    const editUserListMemberFormConfig = await configLoader.getFormConfig("editUserListMember");

    // --- UserList VM (edit form)
    window.userListVM = new UserListViewModel(
        { mode: "edit" },
        { grid: gridConfig, search: searchConfig, form: editUserListFormConfig, actionGroups: actionGroupMap }
    );
    userListVM.validator = await Validator.build(configLoader);
    userListVM.errors = ko.observable({});

    if (editUserListId) {
        await userListVM.loadUserListById(editUserListId);
    }

    // --- UserListMember VM (search + grid)
    window.userListMemberVM = new UserListMemberViewModel(
        { mode: "list", listId: editUserListId },
        { grid: gridConfig, search: searchConfig, form: editUserListMemberFormConfig, actionGroups: actionGroupMap }
    );
    userListMemberVM.validator = await Validator.build(configLoader);
    userListMemberVM.errors = ko.observable({});

    // bind both into KO context
    ko.applyBindings({ userListVM, userListMemberVM }, document.getElementById("userListMembersPage"));

    // load initial members
    await userListMemberVM.loadUserListMembers();
})();
</script>

</body>
</html>
