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
