<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
    <script>
        // Dynamically load knockout-latest.js relative to context path
        const CONTEXT_PATH = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
        const KO_SCRIPT = `${CONTEXT_PATH}/management/js/knockout-latest.js`;
        const script = document.createElement('script');
        script.src = KO_SCRIPT;
        document.head.appendChild(script);
    </script>
</head>
<body>

<button data-bind="click: logout">Logout</button>

<script>
    // Compute login page URL dynamically
    const LOGIN_PAGE = `${CONTEXT_PATH}/public/login.jsp`;

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
