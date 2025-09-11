// validation.js

/**
 * ConfigDrivenViewModel
 * 
 * @param {Object} formConfig - Form configuration (fields array)
 * @param {Object} regexMap - Optional regex validation map (can be empty)
 * @param {Object} options - Optional handlers: { onSave, onCancel, searchTargetVM }
 */
function ConfigDrivenViewModel(formConfig, regexMap = {}, options = {}) {
    const self = this;
    self.formConfig = formConfig;
    self.currentData = {};
    self.errorMessages = ko.observable({});

    // Initialize observables for each field
    formConfig.fields.forEach(f => {
        self.currentData[f.name] = ko.observable('');
    });

    // Optional: link to external target VM (e.g., searchParams)
    if (options.searchTargetVM) {
        self.searchTargetVM = options.searchTargetVM;
    }

    // Validate all fields
    self.validate = function () {
        const errors = {};

        formConfig.fields.forEach(f => {
            const value = self.currentData[f.name]();

            if (f.required && !value.trim()) {
                errors[f.name] = `${f.label} is required`;
            } else if (f.regex && regexMap[f.regex]) {
                const { expression, errorMessage } = regexMap[f.regex];
                const re = new RegExp(expression);
                if (!re.test(value)) {
                    errors[f.name] = errorMessage;
                }
            }
        });

        self.errorMessages(errors);
        return Object.keys(errors).length === 0;
    };

    // Save handler
    self.save = function () {
        if (!self.validate()) {
            console.warn("❌ Validation failed", ko.toJS(self.errorMessages));
            return;
        }

        // If searchTargetVM is linked, propagate values automatically
        if (self.searchTargetVM) {
            Object.keys(self.currentData).forEach(k => {
                if (self.searchTargetVM[k]) self.searchTargetVM[k](self.currentData[k]());
            });
        }

        if (typeof options.onSave === 'function') {
            options.onSave(ko.toJS(self.currentData));
        }
    };

    // Cancel / Reset handler
    self.cancel = function () {
        // Reset linked target VM if present
        if (self.searchTargetVM) {
            Object.keys(self.currentData).forEach(k => {
                if (self.searchTargetVM[k]) self.searchTargetVM[k]('');
            });
        }

        // Reset local currentData observables
        Object.keys(self.currentData).forEach(k => self.currentData[k](''));

        if (typeof options.onCancel === 'function') {
            options.onCancel();
        }
    };
}
