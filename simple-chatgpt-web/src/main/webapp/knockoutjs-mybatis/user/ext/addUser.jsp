<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <script src="../../../js/knockout-latest.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
    <script src="user.js"></script>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Add User</h1>
    <form class="form-vertical" data-bind="submit: saveUser">
        <fieldset>
            <legend>Core Section</legend>
            <div class="form-row"><label>Name:</label><input type="text" data-bind="value: currentUser().name" required></div>
            <div class="form-row"><label>Email:</label><input type="email" data-bind="value: currentUser().email" required></div>
            <div class="form-row"><label>First Name:</label><input type="text" data-bind="value: currentUser().firstName"></div>
            <div class="form-row"><label>Last Name:</label><input type="text" data-bind="value: currentUser().lastName"></div>
            <div class="form-row"><label>Password:</label><input type="password" data-bind="value: currentUser().password"></div>
        </fieldset>
        <fieldset>
            <legend>Additional Address</legend>
            <div class="form-row"><label>Address Line 1:</label><input type="text" data-bind="value: currentUser().addressLine1"></div>
            <div class="form-row"><label>Address Line 2:</label><input type="text" data-bind="value: currentUser().addressLine2"></div>
            <div class="form-row"><label>City:</label><input type="text" data-bind="value: currentUser().city"></div>
            <div class="form-row"><label>State:</label><input type="text" data-bind="value: currentUser().state"></div>
            <div class="form-row"><label>Post Code:</label><input type="text" data-bind="value: currentUser().postCode"></div>
            <div class="form-row"><label>Country:</label><input type="text" data-bind="value: currentUser().country"></div>
        </fieldset>
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