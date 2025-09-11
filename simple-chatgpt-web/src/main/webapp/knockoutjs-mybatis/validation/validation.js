// validation.js
function ConfigDrivenViewModel(formConfig, regexMap, options) {
    const self = this;
    self.formConfig = formConfig;
    self.currentData = {};

    // Create KO observables for each field
    formConfig.fields.forEach(f => {
        self.currentData[f.name] = ko.observable('');
    });

    self.errorMessages = ko.observable({});

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

    // Save handler (uses options if provided)
    self.save = function () {
        if (!self.validate()) {
            console.warn("❌ Validation failed", self.errorMessages());
            return;
        }
        console.log("✅ Valid form data:", ko.toJS(self.currentData));
        if (options && typeof options.onSave === 'function') {
            options.onSave(ko.toJS(self.currentData));
        }
    };

    self.cancel = function () {
        if (options && typeof options.onCancel === 'function') {
            options.onCancel();
        }
    };
}