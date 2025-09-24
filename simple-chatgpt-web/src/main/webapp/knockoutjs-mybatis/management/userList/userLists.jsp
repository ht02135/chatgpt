<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Lists</title>
	<script src="../../../js/knockout-latest.js"></script>
	<script src="userList.js"></script>
	<script src="genericComponents-2.0.js"></script>
    <link rel="stylesheet" href="userList.css">
</head>
<body>

<div id="userListsPage">

    <!-- Search Form -->
    <generic-search-form params="
        searchConfig: objectVM.searchConfig, 
        searchParams: objectVM.searchParams, 
        errors: objectVM.errors, 
        searchObjects: objectVM.searchUserLists,
		coreCount: 3
    "></generic-search-form>

    <!-- Search Actions -->
    <generic-search-actions params="
        addObject: objectVM.navigateToAddUserList,
        searchObjects: objectVM.searchUserLists,
        resetSearch: objectVM.resetSearch
    "></generic-search-actions>

    <!-- Grid -->
    <generic-grid params="
        gridConfig: objectVM.gridConfig,
        items: objectVM.userLists,
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

<!-- Initialization Script -->
<script type="module">
import configLoader from "./configLoader.js";
import Validator from "./validation.js";

(async function () {
    console.log("Initializing User Lists page...");

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("userLists");
    const searchConfig   = await configLoader.getFormConfig("searchUserList");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel (comes from userList.js, global function)
    const objectVM = new UserListViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Build Validator
    objectVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    objectVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ objectVM });

    // Initial load of User Lists
    await objectVM.loadUserLists();
})();
</script>

</body>
</html>
