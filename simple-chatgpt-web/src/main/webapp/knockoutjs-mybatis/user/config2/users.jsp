<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="validation.js"></script>
    <script src="configLoader.js"></script>
    <script src="user.js"></script>
    <link rel="stylesheet" href="user.css">
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>User Management</h1>

    <!-- Search Form -->
    <div class="search-container">
        <form data-bind="submit: searchUsers">
            <div class="form-columns">
                <fieldset class="form-col">
                    <legend>Core Section</legend>
                    <!-- ko foreach: searchConfig.fields.slice(0,3) -->
                    <div class="form-row" data-bind="visible: visible">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.searchParams[name], valueUpdate: 'input'" />
                        </label>
                        <div class="error-message" 
                             data-bind="text: $root.userVM.errors()[name], 
                                        visible: $root.userVM.errors()[name]"></div>
                    </div>
                    <!-- /ko -->
                </fieldset>
                <fieldset class="form-col">
                    <legend>Additional Address</legend>
                    <!-- ko foreach: searchConfig.fields.slice(3) -->
                    <div class="form-row" data-bind="visible: visible">
                        <label>
                            <span data-bind="text: label + ':'"></span>
                            <input type="text" data-bind="value: $parent.searchParams[name], valueUpdate: 'input'" />
                        </label>
                        <div class="error-message" 
                             data-bind="text: $root.userVM.errors()[name], 
                                        visible: $root.userVM.errors()[name]"></div>
                    </div>
                    <!-- /ko -->
                </fieldset>
            </div>
        </form>

        <div class="form-actions">
            <a href="#" data-bind="click: goAddUser">Create User</a>
            <a href="#" data-bind="click: searchUsers">Search</a>
            <a href="#" data-bind="click: resetSearch">Reset</a>
        </div>
    </div>

    <!-- Users Grid -->
    <table>
        <thead>
        <tr data-bind="foreach: gridConfig.columns">
		    <th data-bind="
                           click: function() { if(name !== 'actions') $parent.setSort(name) },
		        style: { cursor: name !== 'actions' ? 'pointer' : 'default' }">

		        <!-- Label + Sort Arrow -->
		        <span>
		            <span data-bind="text: label"></span>
		            <!-- ko if: name !== 'actions' && $parent.sortField() === name -->
		            <span data-bind="text: $parent.sortOrder() === 'ASC' ? ' ▲' : ' ▼'"></span>
		            <!-- /ko -->
		        </span>
		    </th>
        </tr>
        </thead>
        <tbody data-bind="foreach: users">
        <tr data-bind="foreach: $parent.gridConfig.columns">
            <!-- Actions column -->
            <!-- ko if: name === 'actions' -->
            <td>
                <a href="#" data-bind="click: function() { $parents[1].goEditUser($parent.id) }">Edit</a> |
                <a href="#" data-bind="click: function() { $parents[1].deleteUser($parent) }">Delete</a>
            </td>
            <!-- /ko -->

            <!-- Other columns -->
            <!-- ko if: name !== 'actions' -->
            <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
            <!-- /ko -->
        </tr>
        </tbody>
    </table>

	<div class="pagination" style="display: flex; align-items: center; gap: 15px; flex-wrap: nowrap;">
	    <!-- Prev / Next buttons -->
        <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>

		<!-- Page size selector -->
		    <label>
		        Page Size: 
		        <select data-bind="value: size">
		            <option value="10">10</option>
		            <option value="20">20</option>
					<option value="30">30</option>
					<option value="40">40</option>
		            <option value="50">50</option>
		        </select>
		    </label>
		
		<!-- 
		To allow only the arrows, we need to prevent typing but 
		keep the number input editable for arrows
		-->
	    <!-- Page size input (arrows only) -->
		    <label>
	        Page Size: 
		        <input type="number" min="10" max="50" step="10"
	               data-bind="value: size, valueUpdate: 'input'" 
		               style="width:50px;"
		               onkeydown="return event.key === 'ArrowUp' || event.key === 'ArrowDown';"
		               onpaste="return false;"
		               ondrop="return false;">
		    </label>

	    <!-- Total records -->
	    <span>Total: <span data-bind="text: total"></span></span>
    </div>
	
</div>

<script>
(async function(){
    const gridConfig = await configLoader.getGridConfig('users');
    const searchConfig = await configLoader.getFormConfig('searchUser');
    const regexConfig = await configLoader.getRegexMapConfig();

    const userVM = new UserViewModel({ mode: 'list' }, { grid: gridConfig, search: searchConfig });
    userVM.validator = new Validator(regexConfig);
    userVM.errors = ko.observable({});
    ko.applyBindings({ userVM });
    await userVM.loadUsers();
})();
</script>
</body>
</html>
