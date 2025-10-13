<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // ==============================
    // SERVER-SIDE: Read JWT token from cookies
    // ==============================
    String token = null;
    if (request.getCookies() != null) {
        for (javax.servlet.http.Cookie c : request.getCookies()) {
            if ("jwtToken".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
    }

    if (token != null && !token.isEmpty()) {
        // Token exists, redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
        return; // stop further processing
    }
%>

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
         CLIENT-SIDE: Sync JWT from localStorage to cookie
         Runs AFTER page loads in browser
         ============================== -->
    <script>
        // Check if cookie exists
        const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('jwtToken='));
        if (!tokenCookie) {
            // If cookie missing but token in localStorage, set cookie
            const tokenFromStorage = localStorage.getItem('jwtToken');
            if (tokenFromStorage) {
                document.cookie = "jwtToken=" + tokenFromStorage + "; path=/; max-age=" + 24*60*60;
                console.debug("index.jsp -> JWT token synced from localStorage to cookie, reloading page");
                // Reload to trigger server-side redirect
                location.reload();
            } else {
                console.debug("index.jsp -> No JWT token found in localStorage");
            }
        } else {
            console.debug("index.jsp -> JWT token cookie already exists");
        }
    </script>
</body>
</html>
