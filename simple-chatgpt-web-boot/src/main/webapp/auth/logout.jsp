<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
    <script src="../management/js/knockout-latest.js"></script>
</head>
<body>

<button data-bind="click: logout">Logout</button>

<script>
    // ===== Constants =====
    const AUTH_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
    const LOGIN_PAGE = `${AUTH_CONTEXT_PATH}/login.jsp`;

    function LogoutViewModel() {
        const self = this;

        self.logout = function() {
            try {
                // Remove JWT token from localStorage
                localStorage.removeItem('jwtToken');
                console.log("logout.js -> JWT token removed");

                alert('Logged out!');
                // Redirect to login page
                window.location.href = LOGIN_PAGE;
            } catch (err) {
                console.error('Logout error:', err);
                alert('Logout failed: ' + err.message);
            }
        };
    }

    ko.applyBindings(new LogoutViewModel());
</script>

</body>
</html>
