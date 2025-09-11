// validation.js

class Validator {
    constructor(regexArray) {
        this.regexMap = {};
        if (Array.isArray(regexArray)) {
            regexArray.forEach(r => {
                this.regexMap[r.id] = { pattern: new RegExp(r.expression), message: r.errorMessage };
            });
        }
    }

    validateField(fieldConfig, value) {
        if (fieldConfig.required && (!value || !value.trim())) {
            return `${fieldConfig.label} is required`;
        }
        if (fieldConfig.regex && this.regexMap[fieldConfig.regex]) {
            const regex = this.regexMap[fieldConfig.regex].pattern;
            if (!regex.test(value)) return this.regexMap[fieldConfig.regex].message;
        }
        return null;
    }

    validateForm(fields, data) {
        const errors = {};
        fields.forEach(f => {
            const val = ko.unwrap(data[f.name]);
            const err = this.validateField(f, val);
            if (err) errors[f.name] = err;
        });
        return errors;
    }
}
