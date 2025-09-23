// fileUploader.js

class FileUploader {
    constructor(uploadUrl, formConfig = null, validator = null) {
        console.log("fileUploader.js -> constructor:");
        console.log("  uploadUrl=", uploadUrl);
        console.log("  formConfig=", formConfig);
        console.log("  validator=", validator);

        this.uploadUrl = uploadUrl;           // endpoint to POST the file
        this.formConfig = formConfig;         // optional fields for validation
        this.validator = validator;           // optional validator instance
        this.selectedFile = ko.observable(null); // observable for file input
    }

    // -----------------------------
    // Bind file input change event
    // -----------------------------
    onFileSelected(data, event) {
        const file = event.target.files[0];
        console.log("fileUploader.js -> onFileSelected: file=", file);
        this.selectedFile(file);
    }

    // -----------------------------
    // Upload file with optional JSON payload
    // -----------------------------
    async upload(payloadObj = {}) {
        console.log("fileUploader.js -> upload: called");
        const errors = {};

        // Validate form fields
        if (this.validator && this.formConfig?.fields) {
            const fieldErrors = this.validator.validateForm(payloadObj, this.formConfig.fields);
            Object.assign(errors, fieldErrors);
        }

        // Validate file selection
        if (!this.selectedFile()) {
            errors.file = "File is required";
        }

        if (Object.keys(errors).length > 0) {
            console.log("fileUploader.js -> upload: validation errors=", errors);
            return { success: false, errors };
        }

        try {
            const formData = new FormData();
            // Append JSON payload as blob
            formData.append("list", new Blob([JSON.stringify(payloadObj)], { type: "application/json" }));
            // Append file
            formData.append("file", this.selectedFile());

            const res = await fetch(this.uploadUrl, { method: 'POST', body: formData });
            const data = await res.json();
            console.log("fileUploader.js -> upload: response=", data);

            return data;
        } catch (err) {
            console.error("fileUploader.js -> upload error:", err);
            return { success: false, errors: { network: err.message } };
        }
    }
}

export default FileUploader;
