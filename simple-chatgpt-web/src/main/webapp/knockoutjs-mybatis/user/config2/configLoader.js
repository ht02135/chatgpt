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
            console.error("configLoader.js -> loadAll: ❌ Config load error:", err);
            return null;
        }
    },

	async getFormConfig(formId) {
	    console.log("configLoader.js -> getFormConfig: formId=", formId);
	    const data = await this.loadAll();
	    const result = data?.forms.find(f => f.id === formId) || null;
	    //console.log("getFormConfig: result=" + result);
		console.log("configLoader.js -> getFormConfig: result=", result);
	    return result;
	},

	async getGridConfig(gridId) {
	    console.log("configLoader.js -> getGridConfig: gridId=", gridId);
	    const data = await this.loadAll();
	    const result = data?.grids.find(g => g.id === gridId) || null;
		console.log("configLoader.js -> getGridConfig: result=", result);
	    return result;
	},

	async getRegexConfig() {
	    console.log("configLoader.js -> getRegexConfig: called");
	    const data = await this.loadAll();
	    const result = data?.regex || {};
		console.log("configLoader.js -> getRegexConfig: result=", result);
	    return result;
	},
	
	// ✅ New wrapper to convert array to map
	async getRegexMapConfig() {
		console.log("configLoader.js -> getRegexMapConfig: called");
	    const regexArray = await this.getRegexConfig(); // Step 1: get list
	    const regexMap = {};                              // Step 2: build map
	    regexArray.forEach(r => {
	        regexMap[r.id] = r.expression;               // map id → expression
	    });
	    console.log("configLoader.js -> getRegexMapConfig: regexMap=", regexMap); // Step 3: log for debugging
	    return regexMap;                                 // Step 4: return map
	}
};