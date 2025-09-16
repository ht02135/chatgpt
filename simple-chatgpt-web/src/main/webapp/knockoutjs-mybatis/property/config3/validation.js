// validation.js

class Validator {
    constructor(regexConfig = {}) {
        console.log("validation.js -> constructor: regexConfig=", regexConfig);
        this.regexConfig = regexConfig;
    }

	validateField(fieldName, value) {
	    console.log("validation.js -> validateField: fieldName=", fieldName);
	    console.log("validation.js -> validateField: value=", value);

	    if (typeof value !== 'string') value = '' + value;

	    let regexStr = this.regexConfig[fieldName];
	    console.log("validation.js -> validateField: regexStr=", regexStr);

	    if (regexStr) {
	        // Apply case-insensitive flag for boolean type
	        const flags = fieldName.toLowerCase() === 'boolean' ? 'i' : '';
	        const regex = new RegExp(regexStr, flags);

	        if (!regex.test(value)) {
	            console.log("validation.js ##########");
	            console.log("validation.js -> validateField: is invalid");
	            console.log("validation.js -> validateField: fieldName=", fieldName);
	            console.log("validation.js -> validateField: regex=", regex);
	            console.log("validation.js ##########");
	            return `${fieldName} is invalid`;
	        }
	    }

	    return '';
	}

	validateForm(userObj, fieldsConfig) {
	    console.log("validation.js -> validateForm: userObj=", userObj);
		console.log("validation.js -> validateForm: fieldsConfig=", fieldsConfig);
	    const errors = {};
	    
	    fieldsConfig.forEach(f => {
			console.log("validation.js -> validateForm: f=", f);
			
	        const value = userObj[f.name] ? ko.unwrap(userObj[f.name]) : null;
	        const err = this.validateField(f.regex || '', value);
	        
	        console.log("validation.js -> validateForm: value=", value);
	        console.log("validation.js -> validateForm: err=", err);

	        // Instead of trim(), check if value is "empty"
	        const isEmpty = value === null || value === undefined || value === '';

	        if (f.required && isEmpty) {
	            errors[f.name] = `${f.label} is required`;
	        } else if (err) {
	            errors[f.name] = f.errorMessage || err;
	        }
	    });

	    return errors;
	}

}
