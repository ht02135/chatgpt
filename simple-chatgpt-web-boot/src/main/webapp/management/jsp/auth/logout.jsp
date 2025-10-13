<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
    <!-- Directly reference Knockout.js via relative path -->
    <script src="../../js/knockout-latest.js"></script>
</head>
<body>

<button data-bind="click: logout">Logout</button>

<script>
    const CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
    const LOGIN_PAGE = `${CONTEXT_PATH}/management/jsp/auth/login.jsp`;

    function LogoutViewModel() {
        const self = this;

        self.logout = function() {
            try {
                // Remove JWT token from localStorage
                localStorage.removeItem('jwtToken');
                console.log("logout.jsp -> JWT token removed");

                alert('Logged out!');
                // Redirect to login page
                window.location.href = LOGIN_PAGE;
            } catch (err) {
                console.error('Logout error:', err);
                alert('Logout failed: ' + err.message);
            }
        };
    }

    // Apply Knockout bindings after page loads
    window.addEventListener('DOMContentLoaded', () => {
        ko.applyBindings(new LogoutViewModel());
    });
</script>

</body>
</html>
