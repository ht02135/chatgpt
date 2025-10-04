<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Property Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="configLoader.js"></script>
    <script src="property.js"></script>
    <script src="genericComponents.js"></script>
    <link rel="stylesheet" href="property.css">
</head>
<body>
<div class="container" data-bind="with: objectVM">
    <h1>Property Management</h1>

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

<script type="module">
import Validator from "./validation.js"; // correct relative path

(async function () {
    const gridConfig   = await configLoader.getGridConfig("properties");
    const searchConfig = await configLoader.getFormConfig("searchProperty");
    const validatorGroups = await configLoader.getValidatorGroupMap();
    const actionGroupMap  = await configLoader.getActionGroupMap();

    const objectVM = new PropertyViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Validator now builds merged regex config + validatorGroups internally
    objectVM.validator = await Validator.build(configLoader, validatorGroups);

    objectVM.errors = ko.observable({});

    ko.applyBindings({ objectVM });

    // Initial load
    await objectVM.loadProperties();
})();
</script>

</body>
</html>
