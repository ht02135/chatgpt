<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Redirecting...</title>
</head>
<body>
    <h2>Redirecting...</h2>
    <p>If you are not redirected automatically, 
        <a href="<%=request.getContextPath()%>/login.jsp">click here to login</a>.
    </p>

    <!-- ==============================
         CLIENT-SIDE: Check JWT token in localStorage
         Redirect to dashboard if present, else to login
         ============================== -->
    <script>
        // ===== Detect context path dynamically =====
        const CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
        const DASHBOARD_PAGE = CONTEXT_PATH + "/dashboard.jsp";
        const LOGIN_PAGE = CONTEXT_PATH + "/management/jsp/auth/login.jsp";
		console.debug("index.jsp -> CONTEXT_PATH:", CONTEXT_PATH);
		console.debug("index.jsp -> DASHBOARD_PAGE:", DASHBOARD_PAGE);
		console.debug("index.jsp -> LOGIN_PAGE:", LOGIN_PAGE);

        // ===== Check JWT token in localStorage =====
        const jwtToken = localStorage.getItem('jwtToken');

        if (jwtToken && jwtToken.length > 0) {
            console.debug("index.jsp -> JWT token found in localStorage, redirecting to dashboard");
            window.location.href = DASHBOARD_PAGE;
        } else {
            console.debug("index.jsp -> No JWT token in localStorage, redirecting to login");
            window.location.href = LOGIN_PAGE;
        }
    </script>
</body>
</html>
