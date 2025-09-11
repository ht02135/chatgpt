<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<title>Users</title>
<script src="../../../js/knockout-latest.js"></script>
<script src="configLoader.js"></script>
<script src="../../validation/validation.js"></script>
<script src="user.js"></script>
<link rel="stylesheet" href="../../../css/user.css">
<style>.error-message{color:red;font-size:0.9em;}</style>
</head>
<body>
<div data-bind="with: userVM">
    <h1>Users</h1>
    <form data-bind="submit: searchUsers">
        <div data-bind="foreach: searchConfig.fields">
            <label data-bind="text: label+':'"></label>
            <input type="text" data-bind="value: $parent.searchParams[name]">
            <div class="error-message" data-bind="text: $parent.errors()[name], visible: $parent.errors()[name]"></div>
        </div>
        <button type="submit">Search</button>
        <button type="button" data-bind="click: resetSearch">Reset</button>
        <button type="button" data-bind="click: goAddUser">Add User</button>
    </form>

    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr data-bind="foreach: gridConfig.columns">
                <th data-bind="text: label, click:()=>{ if(name!=='actions') $parent.setSort(name)}, style:{cursor:name!=='actions'?'pointer':'default'}"></th>
            </tr>
        </thead>
        <tbody data-bind="foreach: users">
            <tr data-bind="foreach: $parent.gridConfig.columns">
                <!-- ko if: name==='actions' -->
                <td>
                    <a href="#" data-bind="click: ()=>{ $parents[1].goEditUser($parent.id) }">Edit</a> |
                    <a href="#" data-bind="click: ()=>{ $parents[1].deleteUser($parent) }">Delete</a>
                </td>
                <!-- /ko -->
                <!-- ko if: name!=='actions' -->
                <td data-bind="text: $parent[$data.name]? $parent[$data.name]():''"></td>
                <!-- /ko -->
            </tr>
        </tbody>
    </table>

    <div>
        <button data-bind="click: prevPage, enable: page()>1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page()<maxPage()">Next</button>
    </div>
</div>

<script>
(async function(){
    const cfg = await loadConfig();
    const gridConfig = cfg.grids.find(g=>g.id==='users');
    const searchConfig = cfg.forms.find(f=>f.id==='searchUser');
    const regexes = cfg.regex;

    const userVM = new UserViewModel({mode:'list'}, {grid:gridConfig, search:searchConfig}, regexes);
    window.userVM = userVM;
    ko.applyBindings({userVM});
})();
</script>
</body>
</html>
