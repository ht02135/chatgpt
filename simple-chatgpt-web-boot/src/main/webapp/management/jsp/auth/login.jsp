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
    // ===== Detect context path dynamically from browser URL =====
    const CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
    console.debug("login.jsp -> CONTEXT_PATH:", CONTEXT_PATH);

    // ===== Knockout.js script URL (use concatenation, not template literal) =====
    const KO_SCRIPT = CONTEXT_PATH + "/management/js/knockout-latest.js";
    console.debug("login.jsp -> KO_SCRIPT:", KO_SCRIPT);

    // ===== API endpoint and redirect pages =====
    const API_AUTH_LOGIN = CONTEXT_PATH + "/api/management/auth/login";
    const DASHBOARD_PAGE = CONTEXT_PATH + "/dashboard.jsp";
    console.debug("login.jsp -> API_AUTH_LOGIN:", API_AUTH_LOGIN);
    console.debug("login.jsp -> DASHBOARD_PAGE:", DASHBOARD_PAGE);

    // ===== Dynamically load Knockout.js =====
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
                        headers: { 
                            'Content-Type': 'application/json', 
                            'Accept': 'application/json' 
                        },
                        body: JSON.stringify({ 
                            username: self.username(), 
                            password: self.password() 
                        })
                    });

                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

                    const data = await response.json();
                    console.debug("login.jsp -> login response:", data);

                    if (data.token) {
                        localStorage.setItem('jwtToken', data.token);
                        alert('Login successful!');
                        window.location.href = DASHBOARD_PAGE;
                    } else {
                        alert('Login failed: no token returned');
                    }
                } catch (err) {
                    console.error('Login error:', err);
                    alert('Login failed: ' + err.message);
                }
            };
        }

        // Apply Knockout bindings
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
