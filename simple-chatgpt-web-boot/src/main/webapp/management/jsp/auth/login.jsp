<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
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
    // ===== Detect context path dynamically =====
    const CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
    const KO_SCRIPT = CONTEXT_PATH + "/management/js/knockout-latest.js";
    const API_AUTH_LOGIN = CONTEXT_PATH + "/api/management/auth/login";
    const DASHBOARD_PAGE = CONTEXT_PATH + "/dashboard.jsp";

    console.debug("login.jsp -> CONTEXT_PATH:", CONTEXT_PATH);
    console.debug("login.jsp -> API_AUTH_LOGIN:", API_AUTH_LOGIN);

    // ===== Auto-check token from localStorage only =====
    const jwtToken = localStorage.getItem('jwtToken');
    if (jwtToken) {
        console.debug("login.jsp -> token found in localStorage, redirecting to dashboard");
        window.location.href = DASHBOARD_PAGE;
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
