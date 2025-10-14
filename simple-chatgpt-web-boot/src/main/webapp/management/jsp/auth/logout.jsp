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
	// ===== Detect context path dynamically =====
    const CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
    const LOGIN_PAGE = CONTEXT_PATH + "/management/jsp/auth/login.jsp";
	console.debug("logout.jsp -> CONTEXT_PATH:", CONTEXT_PATH);
    console.debug("logout.jsp -> LOGIN_PAGE:", LOGIN_PAGE);

    function LogoutViewModel() {
        const self = this;

        self.logout = function() {
            try {
                // Remove JWT token from localStorage
                localStorage.removeItem('jwtToken');
                console.debug("logout.jsp -> JWT token removed");
				
				// Clear JWT cookie by setting max-age=0
				document.cookie = "jwtToken=; path=/; max-age=0";

                alert('Logged out!');
                // Redirect to login page
                window.location.href = LOGIN_PAGE;
            } catch (err) {
                console.error('Logout error:', err);
                alert('Logout failed: ' + err.message);
            }
        };
    }

    // ===== Apply Knockout bindings after page loads =====
    window.addEventListener('DOMContentLoaded', () => {
        console.debug("logout.jsp -> Applying Knockout bindings");
        ko.applyBindings(new LogoutViewModel());
    });
</script>

</body>
</html>
