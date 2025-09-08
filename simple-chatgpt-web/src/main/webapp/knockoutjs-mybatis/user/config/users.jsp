<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Users List</title>
    <script src="../../../js/knockout-latest.js"></script>
    <script src="../../../js/users.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Users List</h1>
    <div>
        <button data-bind="click: goAddUser">Add User</button>
    </div>
    <table>
        <thead>
        <tr data-bind="foreach: userVM.gridConfig.columns">
            <th data-bind="text: label, click: function() { $parent.setSort(name) }, style: { cursor: 'pointer' }"></th>
        </tr>
        <th>Actions</th>
        </thead>
        <tbody data-bind="foreach: users">
        <tr data-bind="foreach: $parent.gridConfig.columns">
            <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
        </tr>
        <td>
            <a href="#" data-bind="click: function() { $parent.goEditUser(id) }">Edit</a> |
            <a href="#" data-bind="click: function() { $parent.deleteUser($data) }">Delete</a>
        </td>
        </tbody>
    </table>
    <div>
        <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
    </div>
</div>
<script>
    fetch('/chatgpt/api/mybatis/config/all')
        .then(res => res.json())
        .then(cfg => {
            const gridConfig = cfg.grids.find(g => g.id === 'users');
            const userVM = new UserViewModel({ mode: 'list' }, { grid: gridConfig });
            ko.applyBindings({ userVM });
        });
</script>
</body>
</html>