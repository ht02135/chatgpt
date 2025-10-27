// genericComponents.js

//-----------------------------------
// genericFormTitleComponent.js

ko.components.register('generic-form-title', {
  viewModel: function(params) {
    this.formTitle = params.formTitle;
  },
  template: `
    <h1 data-bind="text: formTitle"></h1>
  `
});

//-----------------------------------
// genericFormFieldsComponent.js

ko.components.register('generic-form-fields', {
  viewModel: function(params) {
    this.fields = params.formConfig.fields;
    this.currentObject = params.currentObject;
    this.errors = params.errors;
  },
  template: `
    <div class="form-vertical" data-bind="foreach: fields">
      <div class="form-row">
        <label data-bind="text: label + ':'"></label>
        
        <input type="text"
               data-bind="value: $parent.currentObject()[name], 
                          enable: editable, 
                          valueUpdate: 'input'" />
        
        <div class="error-message"
             data-bind="text: $parent.errors()[name],
                        visible: $parent.errors()[name]"></div>
      </div>
    </div>
  `
});

//-----------------------------------
// genericFormActionsComponent.js

ko.components.register('generic-form-actions', {
  viewModel: function(params) {
    this.saveObject = params.save;
    this.goBack = params.goBack;
  },
  template: `
    <div class="form-actions">
      <button type="submit" data-bind="click: saveObject">Save</button>
      <button type="button" data-bind="click: goBack">Cancel</button>
    </div>
  `
});

//-----------------------------------
// genericComposedFormComponent.js

ko.components.register('generic-composed-form', {
  viewModel: function(params) {
    this.vm = params.vm; 
  },
  template: `
    <div class="container" data-bind="with: vm">
      <generic-form-title params="formTitle: formTitle"></generic-form-title>
      
      <form data-bind="submit: saveObject">
        <generic-form-fields params="formConfig: formConfig, currentObject: currentObject, errors: errors"></generic-form-fields>
        
        <generic-form-actions params="saveObject: saveObject, goBack: goBack"></generic-form-actions>
      </form>
    </div>
  `
});

//-----------------------------------
// genericFormComponent.js

ko.components.register('generic-form', {
    viewModel: function(params) {
        this.vm = params.vm; // no normalization, VM must provide everything
    },

    template: `
        <div class="container" data-bind="with: vm">
            <h1 data-bind="text: formTitle"></h1>
            
            <form data-bind="submit: saveObject">
                <div class="form-vertical" data-bind="foreach: formConfig.fields">
                    <div class="form-row">
                        <label data-bind="text: label + ':'"></label>
                        
                        <input type="text"
                               data-bind="value: $parent.currentObject()[name], 
                                          enable: editable, 
                                          valueUpdate: 'input'" />
                        
                        <div class="error-message"
                             data-bind="text: $parent.errors()[name],
                                        visible: $parent.errors()[name]"></div>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit">Save</button>
                    <button type="button" data-bind="click: goBack">Cancel</button>
                </div>
            </form>
        </div>
    `
});

//-----------------------------------
//-----------------------------------
// genericSearchFormComponent.js

ko.components.register('generic-search-form', {
  viewModel: function(params) {
    this.searchConfig   = params.searchConfig;   // config for fields
    this.searchParams   = params.searchParams;   // observable params
    this.errors         = params.errors;         // validation errors
    this.searchObjects  = params.searchObjects;  // function to trigger search
  },
  template: `
    <div class="search-container">
      <form data-bind="submit: searchObjects">
        <div class="form-columns">
          
          <!-- Core Section -->
          <fieldset class="form-col">
            <legend>Core Section</legend>
            <!-- First 3 fields -->
            <!-- ko foreach: searchConfig.fields.slice(0,3) -->
              <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label + ':', attr: { for: name }"></label>
                <input type="text"
                       data-bind="value: $parent.searchParams[name], valueUpdate: 'input', attr: { id: name, name: name }" />
                <div class="error-message"
                     data-bind="text: $parent.errors()[name], visible: $parent.errors()[name]"></div>
              </div>
            <!-- /ko -->
          </fieldset>
          
          <!-- Additional Section -->
          <fieldset class="form-col">
            <legend>Additional Section</legend>
            <!-- Remaining fields -->
            <!-- ko foreach: searchConfig.fields.slice(3) -->
              <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label + ':', attr: { for: name }"></label>
                <input type="text"
                       data-bind="value: $parent.searchParams[name], valueUpdate: 'input', attr: { id: name, name: name }" />
                <div class="error-message"
                     data-bind="text: $parent.errors()[name], visible: $parent.errors()[name]"></div>
              </div>
            <!-- /ko -->
          </fieldset>
          
        </div>
      </form>
    </div>
  `
});

//-----------------------------------
// genericSearchFormActionsComponent.js

ko.components.register('generic-search-actions', {
  viewModel: function(params) {
    this.goAddObject = params.goAddObject;   // add handler
    this.searchObjects = params.searchObjects;
    this.resetSearch = params.resetSearch;
  },
  template: `
    <div class="form-actions">
      <a href="#" data-bind="click: goAddObject">Create</a>
      <a href="#" data-bind="click: searchObjects">Search</a>
      <a href="#" data-bind="click: resetSearch">Reset</a>
    </div>
  `
});

//-----------------------------------
// genericGridComponent.js

ko.components.register('generic-grid', {
  viewModel: function(params) {
    this.gridConfig = params.gridConfig;
    this.items = params.items;               // generic list (objects, users, properties…)
    this.sortField = params.sortField;
    this.sortOrder = params.sortOrder;
    this.setSort = params.setSort;
    this.getActionsForColumn = params.getActionsForColumn;
    this.invokeAction = params.invokeAction;
  },
  template: `
    <table>
      <thead>
        <tr data-bind="foreach: gridConfig.columns">
          <th data-bind="
            click: function() { if(name !== 'actions') $parent.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }">
            <span>
              <span data-bind="text: label"></span>
              <!-- ko if: name !== 'actions' && $parent.sortField() === name -->
                <span data-bind="text: $parent.sortOrder() === 'ASC' ? ' ▲' : ' ▼'"></span>
              <!-- /ko -->
            </span>
          </th>
        </tr>
      </thead>
      <tbody data-bind="foreach: items">
        <tr data-bind="foreach: $parent.gridConfig.columns">
          <!-- Actions -->
          <!-- ko if: name === 'actions' -->
          <td data-bind="foreach: $component.getActionsForColumn($data)">
            <a href="#"
               data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }
               "></a>
            <!-- separator -->
            <!-- ko if: $index() < $parentContext.$data.length - 1 --> | <!-- /ko -->
          </td>
          <!-- /ko -->

          <!-- Other fields -->
          <!-- ko if: name !== 'actions' -->
          <td data-bind="text: $parent[$data.name] ? $parent[$data.name]() : ''"></td>
          <!-- /ko -->
        </tr>
      </tbody>
    </table>
  `
});

//-----------------------------------
// genericGridPaginationComponent.js

ko.components.register('generic-grid-pagination', {
  viewModel: function(params) {
    this.page = params.page;
    this.maxPage = params.maxPage;
    this.prevPage = params.prevPage;
    this.nextPage = params.nextPage;
    this.size = params.size;
    this.total = params.total;
  },
  template: `
    <div class="pagination" style="display: flex; align-items: center; gap: 15px; flex-wrap: nowrap;">
      <button data-bind="click: prevPage, enable: page() > 1">Prev</button>
      <span data-bind="text: page"></span> / <span data-bind="text: maxPage"></span>
      <button data-bind="click: nextPage, enable: page() < maxPage()">Next</button>

      <label>
        Page Size:
        <select data-bind="value: size">
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="30">30</option>
          <option value="40">40</option>
          <option value="50">50</option>
        </select>
      </label>

      <label>
        Page Size:
        <input type="number" min="10" max="50" step="10"
               data-bind="value: size, valueUpdate: 'input'"
               style="width:50px;"
               onkeydown="return event.key === 'ArrowUp' || event.key === 'ArrowDown';"
               onpaste="return false;" ondrop="return false;">
      </label>

      <span>Total: <span data-bind="text: total"></span></span>
    </div>
  `
});

//-----------------------------------

//-----------------------------------