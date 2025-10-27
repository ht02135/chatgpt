<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
<div id="usersPage">
    <h1>User Management</h1>

    <!-- Search Section -->
    <customize-search-form 
        params="searchConfig: UserVM.searchConfig, 
                searchParams: UserVM.searchParams, 
                errors: UserVM.errors, 
                searchObjects: UserVM.searchObjects,
                coreCount: 5">
    </customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions
        params="actions: UserVM.actionGroupMap['search-user-actions'],
                invokeAction: UserVM.invokeAction">
    </customize-search-actions>

    <!-- Grid Section -->
    <customize-grid 
        params="gridConfig: UserVM.gridConfig, 
                items: UserVM.objects, 
                sortField: UserVM.sortField, 
                sortOrder: UserVM.sortOrder, 
                setSort: UserVM.setSort, 
                getActionsForColumn: UserVM.getActionsForColumn, 
                invokeAction: UserVM.invokeAction">
    </customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination 
        params="page: UserVM.page, 
                maxPage: UserVM.maxPage, 
                prevPage: UserVM.prevPage, 
                nextPage: UserVM.nextPage, 
                size: UserVM.size, 
                total: UserVM.total">
    </customize-grid-pagination>
</div>

<!-- Initialization Script -->
<script type="module">
import { User, UserViewModel } from './user.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing Users page...");

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("users");
    const searchConfig   = await configLoader.getFormConfig("searchUser");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel
    window.UserVM = new UserViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Build Validator
    UserVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    UserVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ UserVM });

    // Initial load of users
    await UserVM.loadUsers();
})();
</script>

</body>
</html>
