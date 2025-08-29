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

    let editingUserId = null;

    // Add/Update User
    $('#addUserForm').submit(function(e) {
        e.preventDefault();
        const user = {
            name: $('#name').val(),
            email: $('#email').val()
        };

        // If editing, add the ID
        if (editingUserId) {
            user.id = editingUserId;
        }

        const url = editingUserId ? `/api/users/${editingUserId}` : '/api/users';
        const method = editingUserId ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(user),
            success: function(response) {
                if (response.status === "SUCCESS") {
                    const action = editingUserId ? 'updated' : 'added';
                    alert(`User ${action} successfully`);
                    resetForm();
                    loadUsers();
                } else {
                    alert('Error: ' + response.message);
                }
            },
            error: function(error) {
                console.error("Save User Network Error:", error);
                alert('Network error. Check console for details.');
            }
        });
    });

    // Edit User
    $(document).on('click', '.editUser', function() {
        const id = $(this).data('id');
        $.get(`/api/users/${id}`, function(response) {
            if (response.status === "SUCCESS") {
                const user = response.data;
                $('#name').val(user.name);
                $('#email').val(user.email);
                editingUserId = user.id;
                $('#submitBtn').text('Update User');
                $('#cancelBtn').show();
            } else {
                alert('Error loading user: ' + response.message);
            }
        }).fail(function(error) {
            console.error("Load User Network Error:", error);
            alert('Failed to load user. Check console for details.');
        });
    });

    // Cancel Edit
    $(document).on('click', '#cancelBtn', function() {
        resetForm();
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
                        loadUsers();
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

    // Reset Form
    function resetForm() {
        $('#addUserForm')[0].reset();
        editingUserId = null;
        $('#submitBtn').text('Add User');
        $('#cancelBtn').hide();
    }

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
                        <td>
                            <button class="editUser" data-id="${user.id}">Edit</button>
                            <button class="deleteUser" data-id="${user.id}">Delete</button>
                        </td>
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