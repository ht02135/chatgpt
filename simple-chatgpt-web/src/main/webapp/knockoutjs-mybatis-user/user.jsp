<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management (Knockout.js)</title>
    <script src="../js/knockoutjs/knockout-latest.js"></script>
    <style>
        /* Copy your CSS from user.html so styling is identical */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 800px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #333; text-align: center; margin-bottom: 30px; }
        form { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #fafafa; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #333; }
        input[type="text"], input[type="email"] { width: 100%; max-width: 300px; padding: 10px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
        button { padding: 10px 20px; margin: 5px; cursor: pointer; border: none; border-radius: 4px; font-size: 14px; font-weight: bold; }
        #submitBtn { background-color: #4CAF50; color: white; }
        #cancelBtn { background-color: #ff9800; color: white; display: none; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f2f2f2; font-weight: bold; color: #333; }
        tbody tr:nth-child(even) { background-color: #f9f9f9; }
        tbody tr:hover { background-color: #f0f8ff; }
        .editUser { background-color: #2196F3; color: white; padding: 6px 12px; font-size: 12px; }
        .deleteUser { background-color: #f44336; color: white; padding: 6px 12px; font-size: 12px; }
    </style>
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
