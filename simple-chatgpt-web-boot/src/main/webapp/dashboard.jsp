<%@ page contentType="text/html;charset=UTF-8" language="java" import="org.apache.commons.text.StringEscapeUtils" %>
<%
    // ==============================
    // SERVER-SIDE: Read JWT token from cookies
    // This runs on the server BEFORE the page is sent to the browser
    // ==============================
    String jwtToken = null;
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (javax.servlet.http.Cookie c : cookies) {
            if ("jwtToken".equals(c.getName())) {
                jwtToken = c.getValue();
                break;
            }
        }
    }
    // Redirect to login if token is missing
    if (jwtToken == null || jwtToken.isEmpty()) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

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
     CLIENT-SIDE: JWT token for API calls
     ============================== -->
<script>
    // Inject token from server-side into JS
    const jwtToken = "<%= StringEscapeUtils.escapeEcmaScript(jwtToken) %>";
    console.debug("dashboard.jsp -> JWT token injected:", jwtToken);

    // Save to localStorage for reuse across pages
    localStorage.setItem('jwtToken', jwtToken);

    // Example: fetch user data using token
    async function fetchUserData() {
        try {
            const response = await fetch('<%= request.getContextPath() %>/api/management/user/data', {
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
</script>

</body>
</html>
