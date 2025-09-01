<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Property</title>
    <link rel="stylesheet" href="../css/property.css">
    <script src="../js/knockoutjs/knockout-latest.js"></script>
    <script src="property.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h2>Edit Property</h2>
<div id="editPropertyForm">
    <form data-bind="submit: done">
        <div>
            Key: <input type="text" data-bind="value: key" readonly />
        </div>
        <div>
            Type: <input type="text" data-bind="value: type" readonly />
        </div>
        <div>
            Value: <input type="text" data-bind="value: value" />
        </div>
        <div>
            <button type="submit">Done</button>
            <button type="button" data-bind="click: cancel">Cancel</button>
        </div>
    </form>
</div>

<script>
    const API_BASE = '/chatgpt/api/mybatis/properties';

    $(function() {
        const urlParams = new URLSearchParams(window.location.search);
        const key = urlParams.get('key');
        if (!key) {
            alert("Missing property key");
            window.location.href = "properties.jsp";
            return;
        }

        $.getJSON(API_BASE + "/" + encodeURIComponent(key), function(resp) {
            if (resp && resp.data) {
                var propertyVM = new EditPropertyViewModel(resp.data);
                ko.applyBindings(propertyVM, document.getElementById("editPropertyForm"));
            } else {
                alert("Property not found");
                window.location.href = "properties.jsp";
            }
        });
    });
</script>
</body>
</html>
