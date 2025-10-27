<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
	<!-- Server-side constants -->
	<%@ include file="/management/include/constants.jspf" %>
	<!-- Client-side constants -->
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
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

<!-- Page Management Section -->
<div class="section">
    <h2>Page Management</h2>
	<p><a href="${pageContext.request.contextPath}/management/jsp/page/pages.jsp">Manage Pages</a></p>
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
	<!-- obsolete
    <p><a href="${pageContext.request.contextPath}/management/jsp/roleGroupRole/roleGroupRoles.jsp">Manage Role Group Roles</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/pageRoleGroup/pageRoleGroups.jsp">Manage Page Role Groups</a></p>
    <p><a href="${pageContext.request.contextPath}/management/jsp/userRoleGroup/userRoleGroups.jsp">Manage User Role Groups</a></p>
	-->
</div>

<hr>

<!-- Role Management Section -->
<div class="section">
    <h2>Open AI Management</h2>
    <p><a href="${pageContext.request.contextPath}/management/jsp/openai/agentcrew/agentCrewProofRead.jsp">Agent Crew Proof Read</a></p>
</div>

<hr>

<!-- Job Request Section -->
<div class="section">
    <h2>Job Request</h2>
    <p><a href="${pageContext.request.contextPath}/management/jsp/batch/jobRequests.jsp">Job Requests</a></p>
</div>

<hr>

<!-- Logout Section -->
<div class="section">
    <p><a href="${pageContext.request.contextPath}/management/jsp/auth/logout.jsp">Logout</a></p>
</div>

<!-- ==============================
     CLIENT-SIDE: Check JWT token from localStorage only
     ============================== -->
<script>
    // ===== Check localStorage for JWT token =====
    const jwtToken = localStorage.getItem('jwtToken');
	if (jwtToken && jwtToken.length > 0) {
	    console.debug("dashboard.jsp -> JWT token found jwtToken=", jwtToken);
	} else {
	    console.debug("http://localhost:8080/chatgpt-production/dashboard.jsp.jsp -> No token found in localStorage, redirecting to login");
	    window.location.href = LOGIN_PAGE;    // from constants.js
	}
</script>

</body>
</html>
