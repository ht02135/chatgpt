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
        <label>First Name:</label>
        <input type="text" data-bind="value: currentUser().firstName">
        <label>Last Name:</label>
        <input type="text" data-bind="value: currentUser().lastName">
        <label>Password:</label>
        <input type="password" data-bind="value: currentUser().password">
        <label>Address Line 1:</label>
        <input type="text" data-bind="value: currentUser().addressLine1">
        <label>Address Line 2:</label>
        <input type="text" data-bind="value: currentUser().addressLine2">
        <label>City:</label>
        <input type="text" data-bind="value: currentUser().city">
        <label>State:</label>
        <input type="text" data-bind="value: currentUser().state">
        <label>Post Code:</label>
        <input type="text" data-bind="value: currentUser().postCode">
        <label>Country:</label>
        <input type="text" data-bind="value: currentUser().country">
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