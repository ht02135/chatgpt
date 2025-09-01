<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Properties List (Knockout.js)</title>
    <link rel="stylesheet" href="../css/property.css">
    <script src="../js/knockoutjs/knockout-latest.js"></script>
    <script src="property.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container" data-bind="with: propertyVM">
    <h1>Properties List</h1>

    <!-- Search Form Start -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 16px; max-width: 600px;">
        <form data-bind="submit: searchProperties">
            <div class="form-columns">
                <fieldset class="form-col">
                    <legend>Search</legend>
                    <div class="form-row">
                        <label>Key:
                            <input type="text" data-bind="value: searchKey, valueUpdate: 'input'">
                        </label>
                    </div>
                    <div class="form-row">
                        <label>Type:
                            <input type="text" data-bind="value: searchType, valueUpdate: 'input'">
                        </label>
                    </div>
                </fieldset>
            </div>
        </form>
        <div class="form-actions">
            <a href="#" data-bind="click: searchProperties">Search</a>
            <a href="#" data-bind="click: resetSearch" style="margin-left: 20px;">Reset</a>
        </div>
    </div>
    <!-- Search Form End -->

    <!-- Table of Properties -->
    <table>
        <thead>
        <tr>
            <th data-bind="click: function() { setSort('key') }" style="cursor:pointer">
                Key <span data-bind="visible: sortField() === 'key', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th data-bind="click: function() { setSort('type') }" style="cursor:pointer">
                Type <span data-bind="visible: sortField() === 'type', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th data-bind="click: function() { setSort('value') }" style="cursor:pointer">
                Value <span data-bind="visible: sortField() === 'value', text: sortOrder() === 'ASC' ? '▲' : '▼'"></span>
            </th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody data-bind="foreach: properties">
        <tr>
            <td data-bind="text: key"></td>
            <td data-bind="text: type"></td>
            <td data-bind="text: value"></td>
            <td>
                <!-- FIX: unwrap observable with key() -->
                <a href="#" data-bind="click: function() { $parent.goEditProperty(key()) }">Edit</a>
            </td>
        </tr>
        </tbody>
    </table>

    <!-- Pagination Controls -->
    <div style="margin-top:20px; text-align:center;">
        <button type="button" data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button type="button" data-bind="click: nextPage, enable: page() < maxPage()">Next</button>

        <span style="margin-left:20px;">
            Page Size:
            <input type="number" min="1" max="100"
                   data-bind="value: size, valueUpdate: 'input'"
                   style="width:50px;">
        </span>
        <span style="margin-left:20px;">Total: <span data-bind="text: total"></span></span>
    </div>
</div>

<script>
    var propertyVM = new PropertyListViewModel({});
    ko.applyBindings({ propertyVM: propertyVM });
</script>
</body>
</html>
