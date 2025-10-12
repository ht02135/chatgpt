<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register</title>
</head>
<body>

<h2>Register</h2>

<form id="registerForm">
    <label>Username: <input type="text" name="userName" required /></label><br><br>
    <label>Password: <input type="password" name="password" required /></label><br><br>
    <label>First Name: <input type="text" name="firstName" required /></label><br><br>
    <label>Last Name: <input type="text" name="lastName" required /></label><br><br>
    <label>Email: <input type="email" name="email" required /></label><br><br>
    <button type="submit">Register</button>
</form>

<script>
// ===== Constants =====
const AUTH_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_AUTH_REGISTER = `${AUTH_CONTEXT_PATH}/api/auth/register`; // API URL with /api
const LOGIN_PAGE = `${AUTH_CONTEXT_PATH}/login.jsp`;

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = {
        userName: e.target.userName.value,
        password: e.target.password.value,
        firstName: e.target.firstName.value,
        lastName: e.target.lastName.value,
        email: e.target.email.value
    };

    try {
        console.log("register.jsp -> submitting register:", API_AUTH_REGISTER);

        const response = await fetch(API_AUTH_REGISTER, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const text = await response.text();
        console.log("register.jsp -> register response:", text);
        alert(text);

        // Optionally redirect to login page after successful registration
        if (text.toLowerCase().includes('success')) {
            window.location.href = LOGIN_PAGE;
        }

    } catch (err) {
        console.error('Register error:', err);
        alert('Registration failed: ' + err.message);
    }
});
</script>

</body>
</html>
