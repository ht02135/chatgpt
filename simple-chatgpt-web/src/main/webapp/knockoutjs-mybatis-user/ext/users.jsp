<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Users List (Knockout.js)</title>
    <script src="../../js/knockoutjs/knockout-latest.js"></script>
    <link rel="stylesheet" href="../../css/user.css">
    <script src="user.js"></script>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Users List</h1>
    <a href="#" data-bind="click: goAddUser">Create User</a>
    <table>
        <thead>
        <tr>
            <th data-bind="click: function() { setSort('id') }" style="cursor:pointer">ID <span data-bind="if: sortField() === 'id'"> <span data-bind="text: sortOrder() === 'ASC' ? '▲' : '▼'"></span></span></th>
            <th data-bind="click: function() { setSort('name') }" style="cursor:pointer">Name <span data-bind="if: sortField() === 'name'"> <span data-bind="text: sortOrder() === 'ASC' ? '▲' : '▼'"></span></span></th>
            <th data-bind="click: function() { setSort('email') }" style="cursor:pointer">Email <span data-bind="if: sortField() === 'email'"> <span data-bind="text: sortOrder() === 'ASC' ? '▲' : '▼'"></span></span></th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody data-bind="foreach: users">
        <tr>
            <td data-bind="text: id"></td>
            <td data-bind="text: name"></td>
            <td data-bind="text: email"></td>
            <td>
                <a href="#" data-bind="click: function() { $parent.goEditUser(id) }">Edit</a> |
                <a href="#" data-bind="click: function() { $parent.deleteUser($data) }">Delete</a>
            </td>
        </tr>
        </tbody>
    </table>
    <div style="margin-top:20px; text-align:center;">
        <button type="button" data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button type="button" data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
        <span style="margin-left:20px;">Page Size: <input type="number" min="1" max="100" data-bind="value: size, valueUpdate: 'input'" style="width:50px;"></span>
        <span style="margin-left:20px;">Total: <span data-bind="text: total"></span></span>
    </div>
</div>
<script>
    var userVM = new UserViewModel({ mode: 'list' });
    ko.applyBindings({ userVM: userVM });
</script>
</body>
</html>