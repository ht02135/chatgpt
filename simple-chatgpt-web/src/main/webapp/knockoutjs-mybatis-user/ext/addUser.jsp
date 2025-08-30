<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../js/knockoutjs/knockout-latest.js"></script>
    <link rel="stylesheet" href="../../css/user.css">
    <script src="user.js"></script>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Add User</h1>
    <form data-bind="submit: saveUser">
        <label>Name:</label>
        <input type="text" data-bind="value: currentUser().name" required>
        <label>Email:</label>
        <input type="email" data-bind="value: currentUser().email" required>
        <div>
            <button type="submit">Done</button>
            <button type="button" data-bind="click: goUsers">Cancel</button>
        </div>
    </form>
</div>
<script>
    var userVM = new UserViewModel({ mode: 'add' });
    ko.applyBindings({ userVM: userVM });
</script>
</body>
</html>