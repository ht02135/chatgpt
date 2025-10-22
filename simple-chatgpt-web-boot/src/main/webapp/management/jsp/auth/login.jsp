<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <%@ include file="/management/include/constants.jspf" %>
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

<script type="module">
import configLoader from '<%= request.getContextPath() %>/management/js/configLoader.js';

// ===== Helper: get cookie value by name =====
function getCookie(name) {
    const matches = document.cookie.match(new RegExp('(?:^|; )' + name + '=([^;]*)'));
    console.debug("login.jsp -> getCookie called, name=", name, ", value=", matches ? matches[1] : null);
    return matches ? matches[1] : null;
}

// ===== Determine dynamic cookie path =====
const cookiePath = '<%= request.getContextPath().isEmpty() ? "/" : request.getContextPath() %>';

// ===== On page load: check token =====
const cookieToken = getCookie('jwtToken');
const localToken = localStorage.getItem('jwtToken');

console.debug("login.jsp -> cookieToken=", cookieToken);
console.debug("login.jsp -> localToken=", localToken);

// Only use non-empty tokens
const tokenToUse = (cookieToken && cookieToken.trim() !== "") ? cookieToken
                 : (localToken && localToken.trim() !== "") ? localToken
                 : null;

if (tokenToUse) {
    console.debug("login.jsp -> Token exists, validating with server, token=", tokenToUse);

    // ===== Validate token with backend =====
    fetch(API_AUTH_VALIDATE, {
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + tokenToUse }
    })
    .then(resp => resp.json())
    .then(respData => {
        console.debug("login.jsp -> validate response:", respData);

        if (resp.ok && respData.success && respData.data.valid) {
            console.debug("login.jsp -> Token valid, syncing to storages and redirecting to dashboard");
            
            // Sync to localStorage
            localStorage.setItem('jwtToken', tokenToUse);
        	console.debug("login.jsp -> token synced to localStorage, token=", localStorage.getItem('jwtToken'));

        	// ===== Save to cookie dynamically =====
        	/*
        	hung : dont remove it
        	Set cookie so server sees it
        	path=/ means the cookie is valid for the entire domain, 
        	from the root (/) down. Every page on your site will 
        	receive this cookie.
        	*/
            document.cookie = `jwtToken=${tokenToUse}; path=${cookiePath}; max-age=${24*60*60}`;
        	console.debug("login.jsp -> token synced to cookie, token=", getCookie('jwtToken'));
            
            window.location.href = DASHBOARD_PAGE;
        } else {
            console.debug("login.jsp -> Token invalid, clearing storage and showing login form");
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('username');
            localStorage.removeItem('roles');
            document.cookie = `jwtToken=; path=${cookiePath}; max-age=0`;
        }
    })
    .catch(err => {
        console.error("login.jsp -> Token validation failed:", err);
        localStorage.removeItem('jwtToken');
        document.cookie = `jwtToken=; path=${cookiePath}; max-age=0`;
    });
} else {
    console.debug("login.jsp -> No token found, showing login form");
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
                console.debug("login.jsp -> submitting login, username=", self.username());

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
					
					// invalidate config cache on successful login
                    if (configLoader && typeof configLoader.invalidateCache === 'function') {
                        console.debug("login.jsp -> invalidating config cache after login success");
                        configLoader.invalidateCache();
                    }

                    const token = loginData.token;
                    console.debug("login.jsp -> login successful, token=", token);

					// save to localStorage
                    localStorage.setItem('jwtToken', token);
                    localStorage.setItem('username', loginData.username || '');
                    localStorage.setItem('roles', JSON.stringify(loginData.roles || []));
					console.debug("login.jsp -> token stored in localStorage, token=", localStorage.getItem('jwtToken'));

					// ===== Save to cookie dynamically =====
					/*
					hung : dont remove it
					Set cookie so server sees it
					path=/ means the cookie is valid for the entire domain, 
					from the root (/) down. Every page on your site will 
					receive this cookie.
					*/
                    document.cookie = `jwtToken=${token}; path=${cookiePath}; max-age=${24*60*60}`;
                    console.debug("login.jsp -> token stored in cookie and localStorage, token=", token);

					    console.debug("login.jsp -> redirecting to dashboard");
                    window.location.href = DASHBOARD_PAGE;
                } else {
                    console.debug("login.jsp -> login failed, no token returned");
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
