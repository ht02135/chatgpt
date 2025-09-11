// validation.js

function Validator(regexArray) {
    this.regexMap = {};
    regexArray.forEach(r => {
        this.regexMap[r.id] = r;
    });

    this.validateForm = function(fields, observables) {
        const errors = {};
        fields.forEach(f => {
            const val = ko.unwrap(observables[f.name]);
            if (f.required && (!val || val.trim() === '')) {
                errors[f.name] = f.label + ' is required';
                return;
            }
            if (f.regex && this.regexMap[f.regex]) {
                const pattern = new RegExp(this.regexMap[f.regex].expression);
                if (!pattern.test(val || '')) {
                    errors[f.name] = this.regexMap[f.regex].errorMessage;
                }
            }
        });
        return errors;
    };
}
