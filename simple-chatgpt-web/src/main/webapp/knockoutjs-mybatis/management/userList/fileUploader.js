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
    }

    // -----------------------------
    // Upload file + payload
    // -----------------------------
    async upload(payloadObj, file) {
        console.log("fileUploader.js -> upload called");
        console.log("fileUploader.js -> payloadObj=", payloadObj);
        console.log("fileUploader.js -> file=", file);

        const errors = {};

        // Optional form validation
        if (this.validator && this.formConfig?.fields) {
            const fieldErrors = this.validator.validateForm(payloadObj, this.formConfig.fields);
            Object.assign(errors, fieldErrors);
        }

        if (!file) {
            errors.file = "File is required";
        }

        if (Object.keys(errors).length > 0) {
            console.log("fileUploader.js -> validation errors=", errors);
            return { success: false, errors };
        }

        try {
            const formData = new FormData();
            // Append JSON payload as blob
            formData.append("list", new Blob([JSON.stringify(payloadObj)], { type: "application/json" }));
            // Append file
            formData.append("file", file);

            const res = await fetch(this.uploadUrl, { method: 'POST', body: formData });
            const data = await res.json();
            console.log("fileUploader.js -> upload response=", data);

            return data;
        } catch (err) {
            console.error("fileUploader.js -> upload error:", err);
            return { success: false, errors: { network: err.message } };
        }
    }

    // -----------------------------
    // Generic file prompt
    // -----------------------------
    promptFile() {
        return new Promise(resolve => {
            const input = document.createElement("input");
            input.type = "file";
            input.style.display = "none";

            input.addEventListener("change", (event) => {
                resolve(event.target.files[0] || null);
                document.body.removeChild(input);
            });

            document.body.appendChild(input);
            input.click();
        });
    }
}

export default FileUploader;
