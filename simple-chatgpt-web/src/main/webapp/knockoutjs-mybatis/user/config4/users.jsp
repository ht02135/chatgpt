<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="../../../js/validation.js"></script>
    <script src="../../../js/configLoader.js"></script>
    <script src="user.js"></script>
    <script src="../../../js/genericComponents.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
<div class="container" data-bind="with: objectVM">
    <h1>User Management</h1>

    <!-- Generic Search Form -->
    <generic-search-form 
        params="searchConfig: searchConfig, 
                searchParams: searchParams, 
                errors: errors, 
                searchObjects: searchObjects">
    </generic-search-form>

    <!-- Generic Search Actions -->
    <generic-search-actions 
        params="goAddObject: goAddObject, 
                searchObjects: searchObjects, 
                resetSearch: resetSearch">
    </generic-search-actions>

    <!-- Generic Grid -->
    <generic-grid 
        params="gridConfig: gridConfig, 
                items: objects, 
                sortField: sortField, 
                sortOrder: sortOrder, 
                setSort: setSort, 
                getActionsForColumn: getActionsForColumn, 
                invokeAction: invokeAction">
    </generic-grid>

    <!-- Generic Pagination -->
    <generic-grid-pagination 
        params="page: page, 
                maxPage: maxPage, 
                prevPage: prevPage, 
                nextPage: nextPage, 
                size: size, 
                total: total">
    </generic-grid-pagination>
</div>

<script>
(async function(){
    const gridConfig   = await configLoader.getGridConfig('users');
    const searchConfig = await configLoader.getFormConfig('searchUser');
    const regexConfig  = await configLoader.getRegexMapConfig();
    const actionGroupMap = await configLoader.getActionGroupMap();

    // rename to objectVM (generic)
    const objectVM = new UserViewModel(
        { mode: 'list' }, 
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    objectVM.validator = new Validator(regexConfig);
    objectVM.errors = ko.observable({});
    ko.applyBindings({ objectVM });
    await objectVM.loadUsers();
})();
</script>
</body>
</html>
