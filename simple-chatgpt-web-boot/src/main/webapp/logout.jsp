<button id="logoutBtn">Logout</button>

<script>
// ===== Constants =====
const AUTH_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const LOGIN_PAGE = `${AUTH_CONTEXT_PATH}/login.jsp`;

/*
reminder, there is no need to really call controller endpoint,
because you really just need to remove jwtToken to end it...
*/

document.getElementById('logoutBtn').addEventListener('click', () => {
    try {
        // Remove JWT token from localStorage
        localStorage.removeItem('jwtToken');
        console.log("logout.js -> JWT token removed");

        alert('Logged out!');
        // Redirect to login page
        window.location.href = LOGIN_PAGE;
    } catch (err) {
        console.error('Logout error:', err);
        alert('Logout failed: ' + err.message);
    }
});
</script>
