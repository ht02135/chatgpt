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