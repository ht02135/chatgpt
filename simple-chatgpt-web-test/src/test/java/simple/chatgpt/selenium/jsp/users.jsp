//////////////////////////////
// users.jsp

<div id="usersPage">
    <h1>User Management</h1>

    <!-- Search Section -->
    <customize-search-form params="searchConfig: UserVM.searchConfig, 
                searchParams: UserVM.searchParams, 
                errors: UserVM.errors, 
                searchObjects: UserVM.searchObjects,
                coreCount: 5">
    <div class="search-container">
      <div class="form-columns">
        
        <!-- Core Section -->
        <fieldset class="form-col">
          <legend>Core Section</legend>
          <!-- First N fields -->
          <!-- ko foreach: $component.searchConfig.fields.slice(0, $component.coreCount) -->
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="userName">Username:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="userName" name="userName">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="userKey">User Key:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="userKey" name="userKey">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="firstName">First Name:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="firstName" name="firstName">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="lastName">Last Name:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="lastName" name="lastName">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="email">Email:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="email" name="email">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          <!-- /ko -->
        </fieldset>
        
        <!-- Additional Section -->
        <fieldset class="form-col">
          <legend>Additional Section</legend>
          <!-- Remaining fields -->
          <!-- ko foreach: $component.searchConfig.fields.slice($component.coreCount) -->
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="addressLine1">Address Line 1:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="addressLine1" name="addressLine1">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="addressLine2">Address Line 2:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="addressLine2" name="addressLine2">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="city">City:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="city" name="city">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="state">State:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="state" name="state">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="postCode">Post Code:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="postCode" name="postCode">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          
            <div class="form-row" data-bind="visible: visible">
              <label data-bind="text: label + ':', attr: { for: name }" for="country">Country:</label>
              <input type="text" data-bind="value: $component.searchParams[name], 
                                valueUpdate: 'input', 
                                attr: { id: name, name: name }" id="country" name="country">
              <div class="error-message" data-bind="text: $component.errors()[name], 
                              visible: $component.errors()[name]" style="display: none;"></div>
            </div>
          <!-- /ko -->
        </fieldset>
      </div>
    </div>
  </customize-search-form>

    <!-- Search Actions -->
    <customize-search-actions params="actions: UserVM.actionGroupMap['search-user-actions'],
                invokeAction: UserVM.invokeAction">
    <div class="form-actions" data-bind="foreach: $component.actions">
      <a href="#" data-bind="
           text: label,
           click: function() { $component.invokeAction($data, $component.rowContext) }">Create</a>
      <!-- separator -->
      <!-- ko if: $index() < $parent.actions.length - 1 --> | <!-- /ko -->
    
      <a href="#" data-bind="
           text: label,
           click: function() { $component.invokeAction($data, $component.rowContext) }">Search</a>
      <!-- separator -->
      <!-- ko if: $index() < $parent.actions.length - 1 --> | <!-- /ko -->
    
      <a href="#" data-bind="
           text: label,
           click: function() { $component.invokeAction($data, $component.rowContext) }">Reset</a>
      <!-- separator -->
      <!-- ko if: $index() < $parent.actions.length - 1 --><!-- /ko -->
    </div>
  </customize-search-actions>

    <!-- Grid Section -->
    <customize-grid params="gridConfig: UserVM.gridConfig, 
                items: UserVM.objects, 
                sortField: UserVM.sortField, 
                sortOrder: UserVM.sortOrder, 
                setSort: UserVM.setSort, 
                getActionsForColumn: UserVM.getActionsForColumn, 
                invokeAction: UserVM.invokeAction">
    <table>
      <thead>
        <tr data-bind="foreach: $component.gridConfig.columns.filter(c =&gt; c.visible)">
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }" style="cursor: pointer;">
            <span>
              <span data-bind="text: label">ID</span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name -->
                <span data-bind="text: $component.sortOrder() === 'ASC' ? ' ▲' : ' ▼'"> ▲</span>
              <!-- /ko -->
            </span>
          </th>
        
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }" style="cursor: pointer;">
            <span>
              <span data-bind="text: label">Username</span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name --><!-- /ko -->
            </span>
          </th>
        
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }" style="cursor: pointer;">
            <span>
              <span data-bind="text: label">First Name</span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name --><!-- /ko -->
            </span>
          </th>
        
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }" style="cursor: pointer;">
            <span>
              <span data-bind="text: label">Last Name</span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name --><!-- /ko -->
            </span>
          </th>
        
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }" style="cursor: pointer;">
            <span>
              <span data-bind="text: label">Email</span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name --><!-- /ko -->
            </span>
          </th>
        
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }" style="cursor: default;">
            <span>
              <span data-bind="text: label">Actions</span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name --><!-- /ko -->
            </span>
          </th>
        </tr>
      </thead>
      <tbody data-bind="foreach: $component.items">
        <tr data-bind="foreach: $parent.gridConfig.columns.filter(c =&gt; c.visible)">
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">16</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02135@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02135@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02135@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02135@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' -->
          <td data-bind="foreach: $component.getActionsForColumn($data)">
            <a href="#" data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }">Edit</a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --><!-- /ko -->
          
            <a href="#" data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }">Delete</a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --><!-- /ko -->
          </td>
          <!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' --><!-- /ko -->

        </tr>
      
        <tr data-bind="foreach: $parent.gridConfig.columns.filter(c =&gt; c.visible)">
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">17</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">99999@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">88888@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">99999@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">99999@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' -->
          <td data-bind="foreach: $component.getActionsForColumn($data)">
            <a href="#" data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }">Edit</a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --><!-- /ko -->
          
            <a href="#" data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }">Delete</a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --><!-- /ko -->
          </td>
          <!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' --><!-- /ko -->

        </tr>
      
        <tr data-bind="foreach: $parent.gridConfig.columns.filter(c =&gt; c.visible)">
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">18</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02137@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02137@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02137@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' --><!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''">ht02137@yahoo.com</td>
          <!-- /ko -->

        
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' -->
          <td data-bind="foreach: $component.getActionsForColumn($data)">
            <a href="#" data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }">Edit</a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --><!-- /ko -->
          
            <a href="#" data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }">Delete</a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --><!-- /ko -->
          </td>
          <!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' --><!-- /ko -->

        </tr>
      </tbody>
    </table>
  </customize-grid>

    <!-- Pagination -->
    <customize-grid-pagination params="page: UserVM.page, 
                maxPage: UserVM.maxPage, 
                prevPage: UserVM.prevPage, 
                nextPage: UserVM.nextPage, 
                size: UserVM.size, 
                total: UserVM.total">
    <div class="pagination" style="display: flex; align-items: center; gap: 15px; flex-wrap: nowrap;">
      <button data-bind="click: $component.prevPage, enable: $component.page() &gt; 1" disabled="">Prev</button>
      <span data-bind="text: $component.page">1</span> / <span data-bind="text: $component.maxPage">1</span>
      <button data-bind="click: $component.nextPage, enable: $component.page() &lt; $component.maxPage()" disabled="">Next</button>

      <label>
        Page Size:
        <select data-bind="value: $component.size">
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="30">30</option>
          <option value="40">40</option>
          <option value="50">50</option>
        </select>
      </label>

      <label>
        Page Size:
        <input type="number" min="10" max="50" step="10" data-bind="value: $component.size, valueUpdate: 'input'" style="width:50px;" onkeydown="return event.key === 'ArrowUp' || event.key === 'ArrowDown';" onpaste="return false;" ondrop="return false;">
      </label>

      <span>Total: <span data-bind="text: $component.total">3</span></span>
    </div>
  </customize-grid-pagination>
</div>
//////////////////////////////