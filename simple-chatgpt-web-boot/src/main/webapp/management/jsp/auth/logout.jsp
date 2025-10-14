<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
	<!-- Server-side constants -->
	<%@ include file="/management/include/constants.jspf" %>
	<!-- Client-side constants -->
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
</head>
<body>

<button data-bind="click: logout">Logout</button>

<script>
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
