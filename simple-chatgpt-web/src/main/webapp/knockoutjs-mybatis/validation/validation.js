// /simple-chatgpt-web/src/main/webapp/knockoutjs-mybatis/validation/validation.js

class Validator {
    constructor(regexConfig = {}) {
        this.regexConfig = regexConfig;
    }

    validateField(fieldName, value) {
        if (typeof value !== 'string') value = '' + value;
        const regex = this.regexConfig[fieldName];
        if (regex && !new RegExp(regex).test(value)) {
            return `${fieldName} is invalid`;
        }
        if (!value.trim()) return `${fieldName} cannot be empty`;
        return '';
    }

    validateForm(user) {
        const errors = {};
        for (const key in user) {
            if (ko.isObservable(user[key])) {
                const err = this.validateField(key, user[key]());
                if (err) errors[key] = err;
            }
        }
        return errors;
    }
}
