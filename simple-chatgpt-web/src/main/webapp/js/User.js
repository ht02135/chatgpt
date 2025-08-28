Here is the **complete updated `User.js`** file with all the fixes and improvements applied:

---

### ✅ **Full `User.js` File**
```javascript
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
                alert('User added successfully');
                $('#addUserForm')[0].reset(); // Clear form
                $('#userList').load('user.html #userList');
            })
            .fail(function(error) {
                console.error("Add User Error:", error);
                alert('Error adding user');
            });
    });

    // Delete User
    $(document).on('click', '.deleteUser', function() {
        const id = $(this).data('id');
        if (confirm('Are you sure you want to delete this user?')) {
            $.ajax({
                url: '/api/users/' + id,
                type: 'DELETE',
                success: function() {
                    alert('User deleted successfully');
                    $('#userList').load('user.html #userList');
                },
                error: function(error) {
                    console.error("Delete User Error:", error);
                    alert('Error deleting user');
                }
            });
        }
    });

    // Load Users
    function loadUsers() {
        $.get('/api/users', function(users) {
            let html = '';
            users.forEach(function(user) {
                html += `<tr>
                    <td>${user.id}</td>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td><button class="deleteUser" data-id="${user.id}">Delete</button></td>
                </tr>`;
            });
            $('#userList tbody').html(html);
        }).fail(function(error) {
            console.error("Load Users Error:", error);
            alert('Failed to load users');
        });
    }

    loadUsers();
});
```

---

### 🔍 **Key Changes Explained**
1. **CSRF Protection**:
   - Added dynamic CSRF token handling using `<meta>` tags in `user.html`.
   - Uses optional chaining (`?.`) to avoid errors if tags are missing.

2. **Relative Paths**:
   - Replaced `/user/user.html` with `user.html` in `.load()` to avoid subcontext issues.

3. **Error Logging**:
   - Added `console.error()` for detailed debugging.
   - Alerts for user feedback.

4. **Form Reset**:
   - Clears the form after successful user addition.

5. **Robust AJAX**:
   - `.done()` and `.fail()` for POST/DELETE/GET requests.

---

### 🛠️ **Required Update in `user.html`**
Add these **meta tags** to the `<head>` section of `user.html` to enable CSRF protection:
```html
<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">
```

---

### ⚠️ **Note**
- If you're **not using Spring Security**, the CSRF section can be removed.
- Ensure the `user.html` file is in the correct location (`/src/main/webapp/user/user.html`).