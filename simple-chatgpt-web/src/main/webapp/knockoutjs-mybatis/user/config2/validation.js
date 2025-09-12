// validation.js

class Validator {
    constructor(regexConfig = {}) {
        console.log("validation.js -> constructor: regexConfig=", regexConfig);
        this.regexConfig = regexConfig;
    }

    validateField(fieldName, value) {
        console.log("validation.js -> validateField:", { fieldName, value });
        if (typeof value !== 'string') value = '' + value;
        const regex = this.regexConfig[fieldName];
        if (regex && !new RegExp(regex).test(value)) {
            return `${fieldName} is invalid`;
        }
        return '';
    }

    validateForm(userObj, fieldsConfig) {
        console.log("validation.js -> validateForm:", { userObj, fieldsConfig });
        const errors = {};
        fieldsConfig.forEach(f => {
            const value = userObj[f.name] ? ko.unwrap(userObj[f.name]) : '';
            const err = this.validateField(f.regex || '', value);
            if (f.required && !value.trim()) {
                errors[f.name] = `${f.label} is required`;
            } else if (err) {
                errors[f.name] = f.errorMessage || err;
            }
        });
        return errors;
    }
}
