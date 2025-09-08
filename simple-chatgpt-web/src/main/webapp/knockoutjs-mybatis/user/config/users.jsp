<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Users List</title>

	<script src="../../../js/knockout-latest.js"></script>
	<link rel="stylesheet" href="../../../css/user.css">
	<script src="user.js"></script>
</head>
<body>
<div class="container" data-bind="with: userVM">
    <h1>Users List</h1>
    <div>
        <button data-bind="click: goAddUser">Add User</button>
    </div>
    <table>
        <thead>
        <tr data-bind="foreach: userVM.gridConfig.columns">
            <th data-bind="text: label, click: function() { $parent.setSort(name) }, style: { cursor: 'pointer' }"></th>
        </tr>
        <th>Actions</th>
        </thead>
        <tbody data-bind="foreach: users">
        <tr data-bind="foreach: $parent.gridConfig.columns">
            <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
        </tr>
        <td>
            <a href="#" data-bind="click: function() { $parent.goEditUser(id) }">Edit</a> |
            <a href="#" data-bind="click: function() { $parent.deleteUser($data) }">Delete</a>
        </td>
        </tbody>
    </table>
    <div>
        <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
        <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
        <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>
    </div>
</div>

<script>
    console.log("➡ Fetching config from /chatgpt/api/mybatis/config/all ...");

    fetch('<c:url value="/api/mybatis/config/all"/>')
        .then(res => {
            console.log("➡ Config fetch response:", res);
            return res.json();
        })
        .then(resp => {
            console.log("✅ Wrapped config JSON:", resp);

            if (resp.status !== "SUCCESS") {
                console.error("❌ Config load failed:", resp.message);
                return;
            }

            const cfg = resp.data;
            console.log("➡ Extracted cfg.data:", cfg);

            const gridConfig = cfg.grids.find(g => g.id === 'users');
            console.log("➡ Selected gridConfig:", gridConfig);

            if (!gridConfig) {
                console.error("❌ No gridConfig found for id=users");
                return;
            }

            // Create view model
            const userVM = new UserViewModel({ mode: 'list' }, { grid: gridConfig });
            console.log("✅ UserViewModel created:", userVM);

            ko.applyBindings({ userVM });
            console.log("✅ Knockout bindings applied.");
        })
        .catch(err => {
            console.error("❌ Failed to load config:", err);
        });
</script>
</body>
</html>
