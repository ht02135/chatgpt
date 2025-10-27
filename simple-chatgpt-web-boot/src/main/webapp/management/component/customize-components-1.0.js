//-----------------------------------
// customize-components-2.0.js
//-----------------------------------

//-----------------------------------
// Form Components
//-----------------------------------

//customize-form-fields
ko.components.register('generic-edit-form-fields', {
  viewModel: function(params) {
    this.fields = (params.formConfig.fields || []).map(f => Object.assign({ type: "text" }, f));
    this.currentObject = params.currentObject;
    this.errors = params.errors || ko.observable({});

    console.log('generic-edit-form-fields: constructor called; fields count=', this.fields.length);
  },
  template: `
    <div class="form-vertical" data-bind="foreach: $component.fields">
      <div class="form-row">
        <label data-bind="text: label + ':'"></label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" />
        <div class="error-message"
             data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]"></div>
      </div>
    </div>
  `
});

//customize-form-actions
ko.components.register('generic-edit-form-actions', {
  viewModel: function(params) {
    // navigateToObjects comes from parent; fallback logs a warning
    this.navigateToObjects = params.navigateToObjects || function() { console.warn('navigateToObjects not provided'); };
    console.log('generic-edit-form-actions: constructor called');
  },
  template: `
    <div class="form-actions">
      <button type="submit">Save</button>
      <button type="button" data-bind="click: navigateToObjects">Cancel</button>
    </div>
  `
});

//customize-form
ko.components.register('generic-edit-form', {
  viewModel: function(params) {
    this.formTitle = params.formTitle || "Edit Form";
    this.formConfig = params.formConfig || { fields: [] };
    this.currentObject = params.currentObject;
    this.errors = params.errors || ko.observable({});
    this.saveObject = params.saveObject || null;
    this.navigateToObjects = params.navigateToObjects || function() { console.warn('navigateToObjects not provided'); };

    console.log('generic-edit-form: constructor called; submitHandler?', !!this.saveObject);
    if (!this.saveObject) {
      console.error('generic-edit-form: No saveObject provided.');
    }
  },
  template: `
    <div class="container">
      <generic-form-title params="formTitle: $component.formTitle"></generic-form-title>
      <form data-bind="submit: $component.saveObject">
        <generic-edit-form-fields 
          params="formConfig: $component.formConfig, 
                  currentObject: $component.currentObject, 
                  errors: $component.errors">
        </generic-edit-form-fields>
        <generic-edit-form-actions 
          params="navigateToObjects: $component.navigateToObjects">
        </generic-edit-form-actions>
      </form>
    </div>
  `
});

//-----------------------------------
// Search Components
//-----------------------------------

//customize-search-form
ko.components.register('generic-search-form', {
  viewModel: function(params) {
    this.searchConfig   = params.searchConfig;
    this.searchParams   = params.searchParams;
    this.errors         = params.errors;
    this.searchObjects  = params.searchObjects;
    this.coreCount      = ko.unwrap(params.coreCount) || 3;
  },
  template: `
    <div class="search-container">
      <form data-bind="submit: $component.searchObjects">
        <div class="form-columns">
          
          <!-- Core Section -->
          <fieldset class="form-col">
            <legend>Core Section</legend>
            <!-- First N fields -->
            <!-- ko foreach: $component.searchConfig.fields.slice(0, $component.coreCount) -->
              <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label + ':', attr: { for: name }"></label>
                <input type="text"
                       data-bind="value: $component.searchParams[name], 
                                  valueUpdate: 'input', 
                                  attr: { id: name, name: name }" />
                <div class="error-message"
                     data-bind="text: $component.errors()[name], 
                                visible: $component.errors()[name]"></div>
              </div>
            <!-- /ko -->
          </fieldset>
          
          <!-- Additional Section -->
          <fieldset class="form-col">
            <legend>Additional Section</legend>
            <!-- Remaining fields -->
            <!-- ko foreach: $component.searchConfig.fields.slice($component.coreCount) -->
              <div class="form-row" data-bind="visible: visible">
                <label data-bind="text: label + ':', attr: { for: name }"></label>
                <input type="text"
                       data-bind="value: $component.searchParams[name], 
                                  valueUpdate: 'input', 
                                  attr: { id: name, name: name }" />
                <div class="error-message"
                     data-bind="text: $component.errors()[name], 
                                visible: $component.errors()[name]"></div>
              </div>
            <!-- /ko -->
          </fieldset>
          
        </div>
      </form>
    </div>
  `
});

//customize-search-actions
ko.components.register('customize-search-actions', {
  viewModel: function(params) {
    this.actions = params.actions;             // array of action configs
    this.invokeAction = params.invokeAction;   // function(action, row)
    this.rowContext = params.rowContext || {}; // optional row data, defaults to {}
  },
  template: `
    <div class="form-actions" data-bind="foreach: $component.actions">
      <a href="#"
         data-bind="
           text: label,
           click: function() { $component.invokeAction($data, $component.rowContext) }"></a>
      <!-- separator -->
      <!-- ko if: $index() < $parent.actions.length - 1 --> | <!-- /ko -->
    </div>
  `
});

//-----------------------------------
// Grid Components
//-----------------------------------

//customize-grid
ko.components.register('generic-grid', {
  viewModel: function(params) {
    this.gridConfig = params.gridConfig;
    this.items = params.items;               // list of objects
    this.sortField = params.sortField;
    this.sortOrder = params.sortOrder;
    this.setSort = params.setSort;
    this.getActionsForColumn = params.getActionsForColumn;
    this.invokeAction = params.invokeAction;
  },
  template: `
    <table>
      <thead>
        <tr data-bind="foreach: $component.gridConfig.columns.filter(c => c.visible)">
          <th data-bind="
            click: function() { if(name !== 'actions') $component.setSort(name) },
            style: { cursor: name !== 'actions' ? 'pointer' : 'default' }">
            <span>
              <span data-bind="text: label"></span>
              <!-- ko if: name !== 'actions' && $component.sortField() === name -->
                <span data-bind="text: $component.sortOrder() === 'ASC' ? ' ▲' : ' ▼'"></span>
              <!-- /ko -->
            </span>
          </th>
        </tr>
      </thead>
      <tbody data-bind="foreach: $component.items">
        <tr data-bind="foreach: $parent.gridConfig.columns.filter(c => c.visible)">
          
          <!-- Actions -->
          <!-- ko if: name === 'actions' -->
          <td data-bind="foreach: $component.getActionsForColumn($data)">
            <a href="#"
               data-bind="
                 text: label,
                 click: function() { $component.invokeAction($data, $parentContext.$parent) }"></a>
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

//customize-grid-pagination
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
      <button data-bind="click: $component.prevPage, enable: $component.page() > 1">Prev</button>
      <span data-bind="text: $component.page"></span> / <span data-bind="text: $component.maxPage"></span>
      <button data-bind="click: $component.nextPage, enable: $component.page() < $component.maxPage()">Next</button>

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
        <input type="number" min="10" max="50" step="10"
               data-bind="value: $component.size, valueUpdate: 'input'"
               style="width:50px;"
               onkeydown="return event.key === 'ArrowUp' || event.key === 'ArrowDown';"
               onpaste="return false;" ondrop="return false;">
      </label>

      <span>Total: <span data-bind="text: $component.total"></span></span>
    </div>
  `
});
