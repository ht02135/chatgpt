<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // ===== Server-side redirect based on token in localStorage cookie or session =====
    // (assuming you store JWT in a cookie named "jwtToken")
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
    } else {
        // No token, redirect to login
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!-- ===== Fallback HTML for users with cookies disabled ===== -->
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
</body>
</html>
