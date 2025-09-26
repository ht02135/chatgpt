// fileUploader.js

class FileUploader {
    /**
     * @param {string} uploadUrl - endpoint to POST the file
     * @param {function(File): Object} payloadProvider - function that returns payload given a File
     */
    constructor(uploadUrl, payloadProvider) {
        console.log("fileUploader.js -> constructor:");
        console.log("  uploadUrl=", uploadUrl);
        console.log("  payloadProvider=", payloadProvider);

        this.uploadUrl = uploadUrl;
        this.payloadProvider = payloadProvider;
    }

    // -----------------------------
    // Upload file
    // -----------------------------
    async upload() {
        console.log("fileUploader.js -> upload called #############");

        try {
            // -----------------------------
            // 1️⃣ Prompt user for file
            // -----------------------------
            const file = await this.promptFile();
            if (!file) {
                console.error("fileUploader.js -> upload: no file selected");
                return { success: false, errors: { file: "File is required" } };
            }
            console.log("fileUploader.js -> upload: file selected=", file.name);

            // -----------------------------
            // 2️⃣ Ask VM to provide payload
            // -----------------------------
            let payloadObj = {};
            if (typeof this.payloadProvider === 'function') {
                payloadObj = this.payloadProvider(file) || {};
            }
            console.log("fileUploader.js -> upload: payloadObj=", payloadObj);

            // -----------------------------
            // 3️⃣ Build FormData
            // -----------------------------
            const formData = new FormData();
            formData.append("list", new Blob([JSON.stringify(payloadObj)], { type: "application/json" }));
            formData.append("file", file);

            // -----------------------------
            // 4️⃣ POST
            // -----------------------------
            const res = await fetch(this.uploadUrl, { method: 'POST', body: formData });
            const data = await res.json();
            console.log("fileUploader.js -> upload: response=", data);

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
