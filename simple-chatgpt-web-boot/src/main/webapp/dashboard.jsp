<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; }
        h1 { color: #333; }
        hr { margin: 20px 0; }
        .section { margin-bottom: 20px; }
        .section p { margin: 5px 0; }
    </style>
</head>
<body>

<h1>Welcome to Simple ChatGPT Dashboard</h1>

<!-- User Management Section -->
<div class="section">
    <h2>User Management</h2>
    <p><a href="${pageContext.request.contextPath}/management/jsp/user/users.jsp">Manage Users</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/userList/userLists.jsp">Manage User Lists</a></p>
</div>

<hr>

<!-- Property Management Section -->
<div class="section">
    <h2>Property Management</h2>
    <p><a href="${pageContext.request.contextPath}/management/jsp/property/properties.jsp">Manage Properties</a></p>
</div>

<hr>

<!-- Role Management Section -->
<div class="section">
    <h2>Role Management</h2>
    <p><a href="${pageContext.request.contextPath}/management/jsp/role/roles.jsp">Manage Roles</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/roleGroup/roleGroups.jsp">Manage Role Groups</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/roleGroupRole/roleGroupRoles.jsp">Manage Role Group Roles</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/pageRoleGroup/pageRoleGroups.jsp">Manage Page Role Groups</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/userRoleGroup/userRoleGroups.jsp">Manage User Role Groups</a></p>
</div>

<hr>

<!-- Logout Section -->
<div class="section">
    <p><a href="${pageContext.request.contextPath}/auth/logout.jsp">Logout</a></p>
</div>

<!-- ==============================
     CLIENT-SIDE: JWT token from localStorage
     ============================== -->
<script>
    // ===== Detect context path dynamically =====
    const CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
    const LOGIN_PAGE = CONTEXT_PATH + "/login.jsp";

    // ===== Check localStorage for JWT token =====
    const jwtToken = localStorage.getItem('jwtToken');

    if (!jwtToken) {
        console.debug("dashboard.jsp -> No token found in localStorage, redirecting to login");
        window.location.href = LOGIN_PAGE;
    } else {
        console.debug("dashboard.jsp -> JWT token found:", jwtToken);

        // Example API call using token
        async function fetchUserData() {
            try {
                const response = await fetch(CONTEXT_PATH + '/api/management/user/data', {
                    headers: {
                        'Authorization': 'Bearer ' + jwtToken,
                        'Content-Type': 'application/json'
                    }
                });
                if (!response.ok) throw new Error('HTTP error! status: ' + response.status);
                const data = await response.json();
                console.log('User data:', data);
            } catch (err) {
                console.error('Error fetching user data:', err);
            }
        }

        fetchUserData();
    }
</script>

</body>
</html>
