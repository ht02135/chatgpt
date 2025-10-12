<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>

<h2>Login</h2>

<form id="loginForm">
    <label>Username: <input type="text" name="username" required /></label><br><br>
    <label>Password: <input type="password" name="password" required /></label><br><br>
    <button type="submit">Login</button>
</form>

<script>
// ===== Constants =====
const AUTH_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_AUTH_LOGIN = `${AUTH_CONTEXT_PATH}/api/auth/login`;
const DASHBOARD_PAGE = `${AUTH_CONTEXT_PATH}/dashboard.jsp`;

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = e.target.username.value;
    const password = e.target.password.value;

    try {
        console.log("login.jsp -> submitting login:", API_AUTH_LOGIN);

        const response = await fetch(API_AUTH_LOGIN, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

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
});
</script>

</body>
</html>
