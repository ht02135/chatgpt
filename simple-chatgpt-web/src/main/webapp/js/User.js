$(document).ready(function() {
    // CSRF setup (if Spring Security is enabled)
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    if (csrfToken && csrfHeader) {
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });
    }

    // Add User
    $('#addUserForm').submit(function(e) {
        e.preventDefault();
        const user = {
            name: $('#name').val(),
            email: $('#email').val()
        };

        $.post('/api/users', user)
            .done(function(response) {
                if (response.status === "SUCCESS") {
                    alert('User added successfully');
                    $('#addUserForm')[0].reset(); // Clear form
                    $('#userList').load('user.html #userList');
                } else {
                    alert('Error: ' + response.message);
                }
            })
            .fail(function(error) {
                console.error("Add User Network Error:", error);
                alert('Network error. Check console for details.');
            });
    });

    // Delete User
    $(document).on('click', '.deleteUser', function() {
        const id = $(this).data('id');
        if (confirm('Are you sure you want to delete this user?')) {
            $.ajax({
                url: '/api/users/' + id,
                type: 'DELETE',
                success: function(response) {
                    if (response.status === "SUCCESS") {
                        alert('User deleted successfully');
                        $('#userList').load('user.html #userList');
                    } else {
                        alert('Error: ' + response.message);
                    }
                },
                error: function(error) {
                    console.error("Delete User Network Error:", error);
                    alert('Network error. Check console for details.');
                }
            });
        }
    });

    // Load Users
    function loadUsers() {
        $.get('/api/users', function(response) {
            if (response.status === "SUCCESS") {
                let html = '';
                response.data.forEach(function(user) {
                    html += `<tr>
                        <td>${user.id}</td>
                        <td>${user.name}</td>
                        <td>${user.email}</td>
                        <td><button class="deleteUser" data-id="${user.id}">Delete</button></td>
                    </tr>`;
                });
                $('#userList tbody').html(html);
            } else {
                alert('Error: ' + response.message);
            }
        }).fail(function(error) {
            console.error("Load Users Network Error:", error);
            alert('Failed to load users. Check console for details.');
        });
    }

    loadUsers();
});
