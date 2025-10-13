<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
</head>
<body>

<button data-bind="click: logout">Logout</button>

<script>
    // ===== Constants =====
    const CONTEXT_PATH = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
    const KO_SCRIPT = `${CONTEXT_PATH}/management/js/knockout-latest.js`;

    // Dynamically load Knockout.js
    const script = document.createElement('script');
    script.src = KO_SCRIPT;
    script.onload = () => {
        // Knockout loaded → now apply bindings
        ko.applyBindings(new LogoutViewModel());
    };
    script.onerror = () => {
        console.error("Failed to load Knockout.js from", KO_SCRIPT);
        alert("Failed to load required scripts. Please refresh or contact admin.");
    };
    document.head.appendChild(script);

    // ===== ViewModel =====
    function LogoutViewModel() {
        const self = this;

        const LOGIN_PAGE = `${CONTEXT_PATH}/public/login.jsp`;

        self.logout = function() {
            try {
                localStorage.removeItem('jwtToken');
                console.log("logout.jsp -> JWT token removed");

                alert('Logged out!');
                window.location.href = LOGIN_PAGE;
            } catch (err) {
                console.error('Logout error:', err);
                alert('Logout failed: ' + err.message);
            }
        };
    }
</script>

</body>
</html>
