<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Users</title>
	<script src="../../../js/knockout-latest.js"></script>
	<link rel="stylesheet" href="../../../css/user.css">
	<script src="user.js"></script>

<!-- 
1>In Knockout.js, $root always refers to the top-level view 
model that you passed to ko.applyBindings(...).
////////////
2>so $root → userVM
-->

<div class="container" data-bind="with: $root">
    <h1>Users List</h1>
    
	<!-- Search Form Start -->
	<div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 600px;">
	    <form data-bind="submit: searchUsers">
	        <div class="form-columns">

	            <!-- Core Section -->
	            <fieldset class="form-col">
	                <legend>Core Section</legend>
	                <!-- ko foreach: searchConfig.fields.slice(0, 3) -->
	                <div class="form-row" data-bind="visible: visible">
	                    <label>
	                        <span data-bind="text: label + ':'"></span>
	                        <input type="text" data-bind="value: $parent.searchParams[name], valueUpdate: 'input'">
	                    </label>
	                </div>
	                <!-- /ko -->
	            </fieldset>

	            <!-- Additional Address Section -->
	            <fieldset class="form-col">
	                <legend>Additional Address</legend>
	                <!-- ko foreach: searchConfig.fields.slice(3) -->
	                <div class="form-row" data-bind="visible: visible">
	                    <label>
	                        <span data-bind="text: label + ':'"></span>
	                        <input type="text" data-bind="value: $parent.searchParams[name], valueUpdate: 'input'">
	                    </label>
	                </div>
	                <!-- /ko -->
	            </fieldset>

	        </div>
	    </form>

	    <div class="form-actions">
	        <a href="#" data-bind="click: goAddUser">Create User</a>
	        <a href="#" data-bind="click: searchUsers" style="margin-left: 20px;">Search</a>
	        <a href="#" data-bind="click: resetSearch" style="margin-left: 20px;">Reset</a>
	    </div>
	</div>
	<!-- Search Form End -->
    
    <table>
        <thead>
        <tr data-bind="foreach: gridConfig.columns">
            <th data-bind="text: label, 
                           click: function() { if(name !== 'actions') $parent.setSort(name) }, 
                           style: { cursor: name !== 'actions' ? 'pointer' : 'default' }"></th>
        </tr>
        </thead>
        <tbody data-bind="foreach: users">
        <tr data-bind="foreach: $parent.gridConfig.columns">
            <!-- ko if: name === 'actions' -->
            <td>
                <a href="#" data-bind="click: function() { $parents[1].goEditUser($parent.id) }">Edit</a> |
                <a href="#" data-bind="click: function() { $parents[1].deleteUser($parent) }">Delete</a>
            </td>
            <!-- /ko -->
            <!-- ko if: name !== 'actions' -->
            <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
            <!-- /ko -->
        </tr>
        </tbody>
    </table>
    <div>
        <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
    </div>
</div>

<script>
console.log("➡ Fetching config from /chatgpt/api/mybatis/config/all ...");
fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        console.log("✅ Wrapped config JSON: ", cfg);
        const data = cfg.data;
        console.log("➡ Extracted cfg.data: ", data);
        
        const gridConfig = data.grids.find(g => g.id === 'users');
        const searchConfig = data.forms.find(f => f.id === 'searchUser');
        
        console.log("➡ Selected gridConfig: ", gridConfig);
        console.log("➡ Selected searchConfig: ", searchConfig);
        
        try {
            const userVM = new UserViewModel(
                { mode: 'list' }, 
                { 
                    grid: gridConfig,
                    search: searchConfig 
                }
            );
            console.log("✅ UserViewModel created: ", userVM);
            ko.applyBindings(userVM);
        } catch(e) {
            console.error("❌ Failed to load config: ", e);
        }
    })
    .catch(err => console.error("❌ Fetch error: ", err));
</script>
</body>
</html>

<!-- 
If you want to be extra safe:
/////////////////////
<script>
document.addEventListener("DOMContentLoaded", function() {
    fetch('/chatgpt/api/mybatis/config/all')
        .then(res => res.json())
        .then(cfg => {
            const data = cfg.data;
            const userVM = new UserViewModel({ mode: 'list' }, { 
                grid: data.grids.find(g => g.id === 'users'),
                search: data.forms.find(f => f.id === 'searchUser')
            });
            ko.applyBindings(userVM);
        });
});
</script>
-->