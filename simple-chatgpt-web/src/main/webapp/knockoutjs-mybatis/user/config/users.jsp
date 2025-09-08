<script src="../../../js/knockout-latest.js"></script>
<link rel="stylesheet" href="../../../css/user.css">
<script src="user.js"></script>

<div class="container" data-bind="with: $root">
    <h1>Users List</h1>
    <div>
        <button data-bind="click: goAddUser">Add User</button>
    </div>
    <table>
        <thead>
        <tr data-bind="foreach: gridConfig.columns">
            <th data-bind="text: label, 
                           click: function() { if(name !== 'actions') $parent.setSort(name) }, 
                           style: { cursor: name !== 'actions' ? 'pointer' : 'default' }"></th>
        </tr>
        </thead>
        <tbody data-bind="foreach: users">
        <tr data-bind="foreach: $parent.gridConfig.columns">
            <!-- ko if: name === 'actions' -->
            <td>
                <a href="#" data-bind="click: function() { $parent.goEditUser($parent.id) }">Edit</a> |
                <a href="#" data-bind="click: function() { $parent.deleteUser($parent) }">Delete</a>
            </td>
            <!-- /ko -->
            <!-- ko if: name !== 'actions' -->
            <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
            <!-- /ko -->
        </tr>
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
fetch('/chatgpt/api/mybatis/config/all')
    .then(res => res.json())
    .then(cfg => {
        console.log("✅ Wrapped config JSON: ", cfg);
        const data = cfg.data;
        console.log("➡ Extracted cfg.data: ", data);
        const gridConfig = data.grids.find(g => g.id === 'users');
        console.log("➡ Selected gridConfig: ", gridConfig);
        try {
            const userVM = new UserViewModel({ mode: 'list' }, { grid: gridConfig });
            console.log("✅ UserViewModel created: ", userVM);
            ko.applyBindings(userVM);
        } catch(e) {
            console.error("❌ Failed to load config: ", e);
        }
    })
    .catch(err => console.error("❌ Fetch error: ", err));
</script>