<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Property Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
	<%@ include file="/management/include/constants.jspf" %>
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
    <link rel="stylesheet" href="property.css">
</head>
<body>
<div id="propertiesPage">
    <h1>Property Management</h1>

    <!-- Search Section -->
    <customize-search-form 
        params="searchConfig: PropertyVM.searchConfig, 
                searchParams: PropertyVM.searchParams, 
                errors: PropertyVM.errors, 
                searchObjects: PropertyVM.searchObjects,
                coreCount: 3">
    </customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions
        params="actions: PropertyVM.actionGroupMap['search-property-actions'],
                invokeAction: PropertyVM.invokeAction">
    </customize-search-actions>

    <!-- Grid Section -->
    <customize-grid 
        params="gridConfig: PropertyVM.gridConfig, 
                items: PropertyVM.objects, 
                sortField: PropertyVM.sortField, 
                sortOrder: PropertyVM.sortOrder, 
                setSort: PropertyVM.setSort, 
                getActionsForColumn: PropertyVM.getActionsForColumn, 
                invokeAction: PropertyVM.invokeAction">
    </customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination 
        params="page: PropertyVM.page, 
                maxPage: PropertyVM.maxPage, 
                prevPage: PropertyVM.prevPage, 
                nextPage: PropertyVM.nextPage, 
                size: PropertyVM.size, 
                total: PropertyVM.total">
    </customize-grid-pagination>
</div>

<!-- Initialization Script -->
<script type="module">
import { Property, PropertyViewModel } from './property.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing Properties page...");

    // Load configs
    const gridConfig     = await configLoader.getGridConfig("properties");
    const searchConfig   = await configLoader.getFormConfig("searchProperty");
    const actionGroupMap = await configLoader.getActionGroupMap();

    // Initialize ViewModel
    window.PropertyVM = new PropertyViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    // Build Validator
    PropertyVM.validator = await Validator.build(configLoader);

    // Initialize observable for errors
    PropertyVM.errors = ko.observable({});

    // Apply Knockout bindings
    ko.applyBindings({ PropertyVM });

    // Initial load of properties
    await PropertyVM.loadProperties();
})();
</script>

</body>
</html>
