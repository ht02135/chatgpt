<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
	<!-- Server-side constants -->
	<%@ include file="/management/include/constants.jspf" %>
	<!-- Client-side constants -->
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
</head>
<body>

<h2>Login</h2>

<form data-bind="submit: submitLogin">
    <label>Username: <input type="text" data-bind="value: username" required /></label><br><br>
    <label>Password: <input type="password" data-bind="value: password" required /></label><br><br>
    <button type="submit">Login</button>
</form>

<p>Don't have an account? <a href="./register.jsp">Register here</a></p>

<script>
    // ===== Auto-check token from localStorage only =====
    const jwtToken = localStorage.getItem('jwtToken');
	console.debug("login.jsp -> jwtToken=", jwtToken);
    if (jwtToken) {
		console.debug("login.jsp -> JWT token found jwtToken=", jwtToken);
		/*
		hung : dont remove it
		Set cookie so server sees it
		path=/ means the cookie is valid for the entire domain, 
		from the root (/) down. Every page on your site will 
		receive this cookie.
		*/
		console.debug("login.jsp -> JWT token found, syncing to cookie");
		document.cookie = `jwtToken=${jwtToken}; path=/; max-age=${24*60*60}`; // 1 day expiration
			
        console.debug("login.jsp -> token found in localStorage, redirecting to dashboard");
        window.location.href = DASHBOARD_PAGE;
    } else {
		console.debug("login.jsp -> No token found in localStorage");
	}

    // ===== Load Knockout.js dynamically =====
    const script = document.createElement('script');
    script.src = KO_SCRIPT;

    script.onload = () => {
        console.debug("login.jsp -> Knockout.js loaded, applying bindings");

        function LoginViewModel() {
            const self = this;
            self.username = ko.observable('');
            self.password = ko.observable('');

            self.submitLogin = async function() {
                try {
                    console.debug("login.jsp -> submitting login:", API_AUTH_LOGIN);

                    const response = await fetch(API_AUTH_LOGIN, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                        body: JSON.stringify({ username: self.username(), password: self.password() })
                    });

                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

                    const responseData = await response.json();
                    console.debug("login.jsp -> login response:", responseData);

                    const loginData = responseData.data;

                    if (loginData && loginData.token) {
                        // Save token to localStorage
                        localStorage.setItem('jwtToken', loginData.token);
                        localStorage.setItem('username', loginData.username || '');
                        localStorage.setItem('roles', JSON.stringify(loginData.roles || []));

                        console.debug("login.jsp -> token stored in localStorage, redirecting to dashboard");
                        window.location.href = DASHBOARD_PAGE;
                    } else {
                        alert(responseData.message || 'Login failed: no token returned');
                    }
                } catch (err) {
                    console.error('login.jsp -> Login error:', err);
                    alert('Login failed: ' + err.message);
                }
            };
        }

        ko.applyBindings(new LoginViewModel());
    };

    script.onerror = () => {
        console.error("login.jsp -> Failed to load Knockout.js from", KO_SCRIPT);
        alert("Failed to load required scripts. Please refresh or contact admin.");
    };

    document.head.appendChild(script);
</script>

</body>
</html>
