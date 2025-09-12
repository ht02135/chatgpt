// validation.js
class Validator {
    constructor(regexConfig = []) {
        this.regexMap = {};
        regexConfig.forEach(r => {
            this.regexMap[r.id] = {
                pattern: r.vaildRegexExpression,
                message: r.errorMessage
            };
        });
    }

    validateField(fieldConfig, value) {
        if (typeof value !== 'string') value = '' + value;

        // Required check
        if (fieldConfig.required && value.trim() === '') {
            return `${fieldConfig.label} cannot be empty`;
        }

        // Regex check
        if (fieldConfig.regex && value.trim() !== '') {
            const regexObj = this.regexMap[fieldConfig.regex];
            if (regexObj) {
                const re = new RegExp(regexObj.pattern);
                if (!re.test(value)) return regexObj.message;
            }
        }

        return '';
    }

    validateForm(obj, fieldsConfig) {
        const errors = {};
        fieldsConfig.forEach(f => {
            const val = ko.unwrap(obj[f.name]);
            const err = this.validateField(f, val);
            if (err) errors[f.name] = err;
        });
        return errors;
    }
}
