// genericFormTitleComponent.js

ko.components.register('generic-form-title', {
  viewModel: function(params) {
    this.formTitle = params.formTitle;
  },
  template: `
    <h1 data-bind="text: formTitle"></h1>
  `
});