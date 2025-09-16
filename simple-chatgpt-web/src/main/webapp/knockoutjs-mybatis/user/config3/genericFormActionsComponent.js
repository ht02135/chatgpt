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