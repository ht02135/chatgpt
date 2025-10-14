<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register</title>
	<!-- Server-side constants -->
	<%@ include file="/management/include/constants.jspf" %>
	<!-- Client-side constants -->
	<script src="<%= request.getContextPath() %>/management/js/constants.js"></script>
</head>
<body>

<h2>Register</h2>

<form data-bind="submit: register">
    <label>Username: <input type="text" data-bind="value: userName" required /></label><br><br>
    <label>Password: <input type="password" data-bind="value: password" required /></label><br><br>
    <label>First Name: <input type="text" data-bind="value: firstName" required /></label><br><br>
    <label>Last Name: <input type="text" data-bind="value: lastName" required /></label><br><br>
    <label>Email: <input type="email" data-bind="value: email" required /></label><br><br>
    <button type="submit">Register</button>
</form>

<script>
    function RegisterViewModel() {
        const self = this;

        self.userName = ko.observable('');
        self.password = ko.observable('');
        self.firstName = ko.observable('');
        self.lastName = ko.observable('');
        self.email = ko.observable('');

        self.register = async function() {
            const formData = {
                userName: self.userName(),
                password: self.password(),
                firstName: self.firstName(),
                lastName: self.lastName(),
                email: self.email()
            };

            try {
                console.debug("register.jsp -> submitting register:", API_AUTH_REGISTER);

                const response = await fetch(API_AUTH_REGISTER, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

                const text = await response.text();
                console.debug("register.jsp -> register response:", text);
                alert(text);

                // Redirect to login page if registration is successful
                if (text.toLowerCase().includes('success')) {
                    window.location.href = LOGIN_PAGE;
                }

            } catch (err) {
                console.error('Register error:', err);
                alert('Registration failed: ' + err.message);
            }
        };
    }

    // ===== Apply Knockout bindings after DOM is ready =====
    window.addEventListener('DOMContentLoaded', () => {
        console.debug("register.jsp -> Applying Knockout bindings");
        ko.applyBindings(new RegisterViewModel());
    });
</script>

</body>
</html>
