<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Property</title>
    <link rel="stylesheet" href="../css/property.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="../js/knockoutjs/knockout-latest.js"></script>
    <script src="property.js"></script>
</head>
<body>
<h2>Edit Property</h2>
<div id="editPropertyForm">
    <form data-bind="submit: done">
        <div>Key: <input type="text" data-bind="value: key" readonly /></div>
        <div>Type: <input type="text" data-bind="value: type" readonly /></div>
        <div>Value: <input type="text" data-bind="value: value" /></div>
        <div>
            <button type="submit">Done</button>
            <button type="button" data-bind="click: cancel">Cancel</button>
        </div>
    </form>
</div>
</body>
</html>
