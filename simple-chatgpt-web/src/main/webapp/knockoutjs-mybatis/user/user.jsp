<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management (Knockout.js)</title>
    <script src="../../js/knockout-latest.js"></script>
    <link rel="stylesheet" href="../../css/user.css">
    <!-- CSS moved to user.css -->
</head>
<body>
<div class="container">
    <h1>User Management System (Knockout.js)</h1>

    <form data-bind="submit: saveUser">
        <h3>Add/Update User</h3>
        <label>Name:</label>
        <input type="text" data-bind="value: currentUser().name" required>
        <label>Email:</label>
        <input type="email" data-bind="value: currentUser().email" required>
        <div>
            <button type="submit" data-bind="text: isEditing() ? 'Update User' : 'Add User'"></button>
            <button type="button" data-bind="click: cancelEdit, visible: isEditing">Cancel</button>
        </div>
    </form>

    <table>
        <thead>
        <tr>
            <th data-bind="click: function() { setSort('id') }" style="cursor:pointer">ID <span data-bind="visible: sortField() === 'id'">▲▼</span></th>
            <th data-bind="click: function() { setSort('name') }" style="cursor:pointer">Name <span data-bind="visible: sortField() === 'name'">▲▼</span></th>
            <th data-bind="click: function() { setSort('email') }" style="cursor:pointer">Email <span data-bind="visible: sortField() === 'email'">▲▼</span></th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody data-bind="foreach: users">
        <tr>
            <td data-bind="text: id"></td>
            <td data-bind="text: name"></td>
            <td data-bind="text: email"></td>
            <td>
                <button data-bind="click: $parent.editUser" class="editUser">Edit</button>
                <button data-bind="click: $parent.deleteUser" class="deleteUser">Delete</button>
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

<script src="user.js"></script>
</body>
</html>