// validation.js
function Validator(regexArray = []) {
    this.regexArray = regexArray;

    this.validateField = function(value, regexId) {
        const val = ko.isObservable(value) ? value() : value;
        if (!val) return false;
        const r = this.regexArray.find(r => r.id === regexId);
        if (!r) return true; // no regex, assume valid
        try {
            const regex = new RegExp(r.validRegexExpression);
            return regex.test(val);
        } catch (e) {
            console.error('Invalid regex:', r, e);
            return false;
        }
    };

    this.validateForm = function(obj, fields) {
        const errors = {};
        let isValid = true;

        fields.forEach(f => {
            const val = ko.isObservable(obj[f.name]) ? obj[f.name]() : obj[f.name];
            if (f.required && (!val || val.toString().trim() === '')) {
                errors[f.name] = f.label + ' is required';
                isValid = false;
            } else if (f.regexId && !this.validateField(val, f.regexId)) {
                const r = this.regexArray.find(r => r.id === f.regexId);
                errors[f.name] = r ? r.errorMessage : f.label + ' invalid';
                isValid = false;
            }
        });

        return { isValid, errors };
    };
}
