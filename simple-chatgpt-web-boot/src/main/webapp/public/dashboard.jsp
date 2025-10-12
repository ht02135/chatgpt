<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
        }
        h1 {
            color: #333;
        }
        hr {
            margin: 20px 0;
        }
        .section {
            margin-bottom: 20px;
        }
        .section p {
            margin: 5px 0;
        }
    </style>
</head>
<body>

<h1>Welcome to Simple ChatGPT Web</h1>

<!-- User Management Section -->
<div class="section">
    <h2>User Management</h2>
    <p><a href="/management/jsp/user/users.jsp">Manage Users</a></p>
    <p><a href="/management/jsp/userList/userLists.jsp">Manage User Lists</a></p>
</div>

<hr>

<!-- Property Management Section -->
<div class="section">
    <h2>Property Management</h2>
    <p><a href="/management/jsp/property/properties.jsp">Manage Properties</a></p>
</div>

<hr>

<!-- Role Management Section -->
<div class="section">
    <h2>Role Management</h2>
    <p><a href="/management/jsp/role/roles.jsp">Manage Roles</a></p>
    <p><a href="/management/jsp/roleGroup/roleGroups.jsp">Manage Role Groups</a></p>
    <p><a href="/management/jsp/roleGroupRole/roleGroupRoles.jsp">Manage Role Group Roles</a></p>
    <p><a href="/management/jsp/pageRoleGroup/pageRoleGroups.jsp">Manage Page Role Groups</a></p>
    <p><a href="/management/jsp/userRoleGroup/userRoleGroups.jsp">Manage User Role Groups</a></p>
</div>

<hr>

<!-- Logout -->
<div class="section">
    <p><a href="/public/logout.jsp">Logout</a></p>
</div>

</body>
</html>
