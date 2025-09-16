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