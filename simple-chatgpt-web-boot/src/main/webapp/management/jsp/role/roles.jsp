<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Role Management</title>
    <script src="../../js/knockout-latest.js"></script>
    <script src="../../component/customize-components-3.0.js"></script>
    <link rel="stylesheet" href="role.css">
</head>
<body>
<div id="rolesPage">
    <h1>Role Management</h1>

    <customize-search-form 
        params="searchConfig: RoleVM.searchConfig, 
                searchParams: RoleVM.searchParams, 
                errors: RoleVM.errors, 
                searchObjects: RoleVM.searchObjects,
                coreCount: 5">
    </customize-search-form>

    <customize-search-actions
        params="actions: RoleVM.actionGroupMap['search-role-actions'],
                invokeAction: RoleVM.invokeAction">
    </customize-search-actions>

    <customize-grid 
        params="gridConfig: RoleVM.gridConfig, 
                items: RoleVM.objects, 
                sortField: RoleVM.sortField, 
                sortOrder: RoleVM.sortOrder, 
                setSort: RoleVM.setSort, 
                getActionsForColumn: RoleVM.getActionsForColumn, 
                invokeAction: RoleVM.invokeAction">
    </customize-grid>

    <customize-grid-pagination 
        params="page: RoleVM.page, 
                maxPage: RoleVM.maxPage, 
                prevPage: RoleVM.prevPage, 
                nextPage: RoleVM.nextPage, 
                size: RoleVM.size, 
                total: RoleVM.total">
    </customize-grid-pagination>
</div>

<script type="module">
import { Role, RoleViewModel } from './role.js';
import configLoader from "../../js/configLoader.js";
import Validator from "../../js/validation.js";

(async function () {
    console.log("Initializing Roles page...");

    const gridConfig     = await configLoader.getGridConfig("roles");
    const searchConfig   = await configLoader.getFormConfig("searchRole");
    const actionGroupMap = await configLoader.getActionGroupMap();

    window.RoleVM = new RoleViewModel(
        { mode: "list" },
        { grid: gridConfig, search: searchConfig, actionGroups: actionGroupMap }
    );

    RoleVM.validator = await Validator.build(configLoader);
    RoleVM.errors = ko.observable({});

    ko.applyBindings({ RoleVM });
    await RoleVM.loadRoles();
})();
</script>

</body>
</html>
