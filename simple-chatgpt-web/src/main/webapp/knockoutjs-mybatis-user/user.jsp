<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management (Knockout.js)</title>
    <script src="../js/knockoutjs/knockout-latest.js"></script>
    <link rel="stylesheet" href="../css/user.css">
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
        <tr><th>ID</th><th>Name</th><th>Email</th><th>Actions</th></tr>
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
</div>

<script src="user.js"></script>
</body>
</html>