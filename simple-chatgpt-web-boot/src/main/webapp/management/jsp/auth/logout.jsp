<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
    <%@ include file="/management/include/constants.jspf" %>
    <script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
</head>
<body>
<h2>Logging out...</h2>

<script>
    // Helper to clear a cookie
    function clearCookie(name) {
        document.cookie = name + "=; path=/; max-age=0";
        console.debug("logout.jsp -> cleared cookie", name);
    }

    async function doLogout() {
        try {
            console.debug("logout.jsp -> calling logout controller");

            const response = await fetch(API_AUTH_LOGOUT, {
                method: 'POST',
                headers: { 'Accept': 'application/json' }
            });

            const result = await response.json();
            console.debug("logout.jsp -> logout response:", result);

            // Clear localStorage
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('username');
            localStorage.removeItem('roles');
            console.debug("logout.jsp -> localStorage cleared");

            // Clear cookie as fallback (server should clear too)
            clearCookie('jwtToken');

            // Redirect to login page
            console.debug("logout.jsp -> redirecting to login page");
            window.location.href = LOGIN_PAGE;

        } catch (err) {
            console.error("logout.jsp -> Logout failed:", err);
            alert("Logout failed: " + err.message);
        }
    }

    // Trigger logout immediately on page load
    doLogout();
</script>

</body>
</html>
