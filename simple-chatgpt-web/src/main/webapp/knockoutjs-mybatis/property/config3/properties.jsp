<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Property Management</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="validation.js"></script>
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

<script>
(async function(){
    const gridConfig      = await configLoader.getGridConfig('properties');
    const searchConfig    = await configLoader.getFormConfig('searchProperty');
    const validatorGroups = await configLoader.getValidatorGroupMap();
	console.log("properties.jsp ##########");
	console.log("properties.jsp -> validatorGroups=", validatorGroups);
	console.log("properties.jsp -> validatorGroups JSON=", JSON.stringify(validatorGroups, null, 2));
	console.log("properties.jsp ##########");
    const actionGroupMap  = await configLoader.getActionGroupMap();

    // rename to objectVM (generic)
    const objectVM = new PropertyViewModel(
        { mode: 'list' }, 
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap, validatorGroups: validatorGroups }
    );

    objectVM.validator = new Validator(validatorGroups); // use type-based validators
    objectVM.errors = ko.observable({});

    ko.applyBindings({ objectVM });

    // initial load
    await objectVM.loadProperties();
})();
</script>


</body>
</html>
