<button id="logoutBtn">Logout</button>

<script>
document.getElementById('logoutBtn').addEventListener('click', () => {
    // Remove JWT token from localStorage (or cookie)
    localStorage.removeItem('jwtToken');

    alert('Logged out!');
    window.location.href = '/login.jsp'; // redirect to login
});
</script>
