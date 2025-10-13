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
    // ===== Constants =====
    const CONTEXT_PATH = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
    const KO_SCRIPT = `${CONTEXT_PATH}/management/js/knockout-latest.js`;

    // Dynamically load Knockout.js
    const script = document.createElement('script');
    script.src = KO_SCRIPT;
    script.onload = () => {
        // Knockout loaded → now apply bindings
        ko.applyBindings(new LoginViewModel());
    };
    script.onerror = () => {
        console.error("Failed to load Knockout.js from", KO_SCRIPT);
        alert("Failed to load required scripts. Please refresh or contact admin.");
    };
    document.head.appendChild(script);

    // ===== ViewModel =====
    function LoginViewModel() {
        const self = this;

        self.username = ko.observable('');
        self.password = ko.observable('');

        self.submitLogin = async function() {
            const API_AUTH_LOGIN = `${CONTEXT_PATH}/api/auth/login`;
            const DASHBOARD_PAGE = `${CONTEXT_PATH}/public/dashboard.jsp`;

            try {
                console.log("login.jsp -> submitting login:", API_AUTH_LOGIN);

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
                console.log("login.jsp -> login response:", data);

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
</script>

</body>
</html>
