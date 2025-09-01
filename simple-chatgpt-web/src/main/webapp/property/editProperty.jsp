<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String key = request.getParameter("key");
    // You may want to fetch the property from backend here
%>
<html>
<head>
    <title>Edit Property</title>
    <script src="/js/knockoutjs/knockout-latest.js"></script>
    <script src="/property/property.js"></script>
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
    // Fetch property details from backend
    $.getJSON("/properties/all", { key: "<%=key%>" }, function(resp) {
        var prop = { key: "", type: "", value: "" };
        if (resp && resp.data && resp.data.properties) {
            var found = resp.data.properties.find(function(p) { return p.key === "<%=key%>"; });
            if (found) prop = found;
        }
        ko.applyBindings(new EditPropertyViewModel(prop), document.getElementById('editPropertyForm'));
    });
</script>
</body>
</html>
