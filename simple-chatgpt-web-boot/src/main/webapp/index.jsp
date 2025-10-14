<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Redirecting...</title>
	<!-- Server-side constants -->
	<%@ include file="/management/include/constants.jspf" %>
	<!-- Client-side constants -->
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
</head>
<body>

<h2>Redirecting...</h2>
<p>If you are not redirected automatically, 
    <a href="<%= LOGIN_PAGE %>">click here to login</a>
</p>

<script>
    // ===== Check JWT token in localStorage =====
    const jwtToken = localStorage.getItem('jwtToken');
    if (jwtToken && jwtToken.length > 0) {
        console.debug("index.jsp -> JWT token found, redirecting to dashboard jwtToken=", jwtToken);
        window.location.href = DASHBOARD_PAGE; // from constants.js
    } else {
        console.debug("index.jsp -> No JWT token, redirecting to login");
        window.location.href = LOGIN_PAGE;    // from constants.js
    }
</script>

</body>
</html>
