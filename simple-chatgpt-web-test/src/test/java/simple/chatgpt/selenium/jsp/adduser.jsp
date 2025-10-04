//////////////////////////////
// adduser.jsp

<customize-form params="
	    formTitle: 'Add User',
	    formConfig: userVM.formConfig,
	    currentObject: userVM.currentObject,
	    errors: userVM.errors,
	    actions: userVM.actionGroupMap['user-form-actions'],
	    invokeAction: userVM.invokeAction">
    <div class="container">
      <generic-form-title params="formTitle: $component.formTitle"></generic-form-title>
      <div>
        <customize-form-fields params="formConfig: $component.formConfig, 
                  currentObject: $component.currentObject, 
                  errors: $component.errors">
    <div class="form-vertical" data-bind="foreach: $component.fields">
      <div class="form-row">
        <label data-bind="text: label + ':'">Username:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">User Key:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">First Name:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Last Name:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Email:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Password:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Address Line 1:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Address Line 2:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">City:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">State:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Post Code:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    
      <div class="form-row">
        <label data-bind="text: label + ':'">Country:</label>
        <input data-bind="
               attr: { type: type },
               value: $component.currentObject()[name],
               enable: editable,
               valueUpdate: 'input'" type="text">
        <div class="error-message" data-bind="text: $component.errors()[name],
                        visible: $component.errors()[name]" style="display: none;"></div>
      </div>
    </div>
  </customize-form-fields>
        <customize-form-actions params="actions: $component.actions,
                  invokeAction: $component.invokeAction">
    <div class="form-actions" data-bind="foreach: $component.actions">
      <a href="#" data-bind="
           text: label,
           click: function() { $component.invokeAction($data, $component.rowContext) }">Save</a>
      <!-- separator -->
      <!-- ko if: $index() < $parent.actions.length - 1 --> | <!-- /ko -->
    
      <a href="#" data-bind="
           text: label,
           click: function() { $component.invokeAction($data, $component.rowContext) }">Cancel</a>
      <!-- separator -->
      <!-- ko if: $index() < $parent.actions.length - 1 --><!-- /ko -->
    </div>
  </customize-form-actions>
      </div>
    </div>
  </customize-form>
  //////////////////////////////