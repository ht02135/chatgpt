// validation.js

class Validator {
    constructor(regexConfig = {}, validatorGroups = {}) {
        console.log("validation.js -> constructor:");
        console.log("  regexConfig=", regexConfig);
        console.log("  validatorGroups=", validatorGroups);

        this.regexConfig = regexConfig;     // merged config
        this.validatorGroups = validatorGroups;
    }

    // -----------------------------
    // Static async builder
    // -----------------------------
    static async build(configLoader, validatorGroupsMap = null) {
        // Always load regex map (used in user forms)
        const regexConfig = await configLoader.getRegexMapConfig();

        let mergedConfig = { ...regexConfig };

        // If validatorGroupsMap is provided, merge validatorRegexConfig
        if (validatorGroupsMap) {
            const validatorRegexConfig =
                await configLoader.buildValidatorRegexConfig(validatorGroupsMap);

            mergedConfig = {
                ...mergedConfig,           // user regexes
                ...validatorRegexConfig    // property regexes (override if same key)
            };
        }

        return new Validator(mergedConfig, validatorGroupsMap || {});
    }

    // -----------------------------
    // Resolve which regex key to use
    // -----------------------------
    getFieldName(f, obj) {
        if (f.regex) {
            return f.regex; // direct regex (user form style)
        }

        if (f.validatorsId && this.validatorGroups) {
            const validatorGroup = this.validatorGroups[f.validatorsId];
            if (validatorGroup) {
                const type = ko.unwrap(obj["type"]);
                const typeKey = type ? type.toLowerCase() : null;

                if (typeKey) {
                    const v = validatorGroup.find(
                        v => v.type.toLowerCase() === typeKey
                    );
                    if (v) {
                        return v.type.toLowerCase();
                    }
                }
            }
        }

        return null;
    }

    // -----------------------------
    // Validate a single field
    // -----------------------------
    validateField(fieldName, value, customMessage) {
        if (typeof value !== "string") value = "" + value;

        const regexStr = this.regexConfig[fieldName];
        if (regexStr) {
            const flags = fieldName.toLowerCase() === "boolean" ? "i" : "";
            const regex = new RegExp(regexStr, flags);

            if (!regex.test(value)) {
                return customMessage || `${fieldName} is invalid`;
            }
        }
        return "";
    }

    // -----------------------------
    // Validate the entire form
    // -----------------------------
    validateForm(obj, fieldsConfig) {
        const errors = {};

        fieldsConfig.forEach(f => {
            const fieldName = this.getFieldName(f, obj);
            const value = obj[f.name] ? ko.unwrap(obj[f.name]) : null;

            let err = "";
            const isEmpty = value === null || value === undefined || value === "";

            if (f.required && isEmpty) {
                err = `${f.label || f.name} is required`;
            } else if (fieldName) {
                err = this.validateField(fieldName, value, f.errorMessage);
            }

            if (err) errors[f.name] = err;
        });

        return errors;
    }
}

export default Validator;
