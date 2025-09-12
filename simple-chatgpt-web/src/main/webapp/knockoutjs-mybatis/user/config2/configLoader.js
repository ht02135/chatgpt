// configLoader.js

const API_BASE = '/chatgpt/api/mybatis/config';

const configLoader = {
    async loadAll() {
        console.log("configLoader.js -> loadAll: called");
        try {
            const res = await fetch(`${API_BASE}/all`);
            const json = await res.json();
            if (json.status !== 'SUCCESS') throw new Error('Failed to load config');
			//console.log("configLoader.js -> loadAll: json.data=" + json.data);
			console.log("configLoader.js -> loadAll: json.data=", json.data);
            return json.data;
        } catch (err) {
            console.error("❌ Config load error:", err);
            return null;
        }
    },

	async getFormConfig(formId) {
	    console.log("configLoader.js -> getFormConfig: formId=", formId);
	    const data = await this.loadAll();
	    const result = data?.forms.find(f => f.id === formId) || null;
	    //console.log("getFormConfig: result=" + result);
		console.log("getFormConfig: result=", result);
	    return result;
	},

	async getGridConfig(gridId) {
	    console.log("configLoader.js -> getGridConfig: gridId=", gridId);
	    const data = await this.loadAll();
	    const result = data?.grids.find(g => g.id === gridId) || null;
		console.log("getGridConfig: result=", result);
	    return result;
	},

	async getRegexConfig() {
	    console.log("configLoader.js -> getRegexConfig: called");
	    const data = await this.loadAll();
	    const result = data?.regex || {};
		console.log("getRegexConfig: result=", result);
	    return result;
	}
};