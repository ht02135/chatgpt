<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Users List (Knockout.js)</title>
    <script src="../../../js/knockout-latest.js"></script>
    <link rel="stylesheet" href="../../../css/user.css">
    <script src="user.js"></script>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Users List</h1>
    <!-- Search Form Start -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 600px;">
        <form data-bind="submit: searchUsers">
            <div class="form-columns">
                <fieldset class="form-col">
                    <legend>Core Section</legend>
                    <div class="form-row"><label>First Name: <input type="text" data-bind="value: searchFirstName, valueUpdate: 'input'"></label></div>
                    <div class="form-row"><label>Last Name: <input type="text" data-bind="value: searchLastName, valueUpdate: 'input'"></label></div>
                    <div class="form-row"><label>Email: <input type="text" data-bind="value: searchEmail, valueUpdate: 'input'"></label></div>
                </fieldset>
                <fieldset class="form-col">
                    <legend>Additional Address</legend>
                    <div class="form-row"><label>Address Line 1: <input type="text" data-bind="value: searchAddressLine1, valueUpdate: 'input'"></label></div>
                    <div class="form-row"><label>Address Line 2: <input type="text" data-bind="value: searchAddressLine2, valueUpdate: 'input'"></label></div>
                    <div class="form-row"><label>City: <input type="text" data-bind="value: searchCity, valueUpdate: 'input'"></label></div>
                    <div class="form-row"><label>State: <input type="text" data-bind="value: searchState, valueUpdate: 'input'"></label></div>
                    <div class="form-row"><label>Country: <input type="text" data-bind="value: searchCountry, valueUpdate: 'input'"></label></div>
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
        <tr>
            <th data-bind="click: function() { setSort('id') }" style="cursor:pointer">
                ID <span data-bind="visible: sortField() === 'id', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th data-bind="click: function() { setSort('firstName') }" style="cursor:pointer">
                First Name <span data-bind="visible: sortField() === 'firstName', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th data-bind="click: function() { setSort('lastName') }" style="cursor:pointer">
                Last Name <span data-bind="visible: sortField() === 'lastName', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th data-bind="click: function() { setSort('email') }" style="cursor:pointer">
                Email <span data-bind="visible: sortField() === 'email', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody data-bind="foreach: users">
        <tr>
            <td data-bind="text: id"></td>
            <td data-bind="text: firstName"></td>
            <td data-bind="text: lastName"></td>
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