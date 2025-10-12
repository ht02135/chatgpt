<form id="loginForm">
    <input type="text" name="username" placeholder="Username" />
    <input type="password" name="password" placeholder="Password" />
    <button type="submit">Login</button>
</form>

<script>
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = e.target.username.value;
    const password = e.target.password.value;

    const response = await fetch('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });

    const data = await response.json();
    if (data.token) {
        // Store JWT in localStorage (or cookie)
        localStorage.setItem('jwtToken', data.token);
        alert('Login successful!');
        window.location.href = '/user/dashboard.jsp'; // redirect after login
    } else {
        alert('Login failed');
    }
});
</script>
