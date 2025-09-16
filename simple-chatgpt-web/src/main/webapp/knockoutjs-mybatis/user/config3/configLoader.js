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
	},
	
	async getActionGroups() {
	    console.log("configLoader.js -> getActionGroups: called");
	    const data = await this.loadAll();
	    const result = data?.actions || [];
	    console.log("configLoader.js -> getActionGroups: result=", result);
	    return result;
	},

	async getActionGroup(actionGroupId) {
	    console.log("configLoader.js -> getActionGroup: actionGroupId=", actionGroupId);
	    const actionGroups = await this.getActionGroups();
	    const actionGroup = actionGroups.find(g => g.id === actionGroupId) || null;

	    if (actionGroup) {
	        // keep only visible actions
	        actionGroup.actions = actionGroup.actions.filter(a => a.visible);
	    }

	    console.log("configLoader.js -> getActionGroup: result=", actionGroup);
	    return actionGroup;
	},

	async getActionGroupMap() {
	    console.log("configLoader.js -> getActionGroupMap: called");
	    const actionGroups = await this.getActionGroups();
	    const map = {};
	    actionGroups.forEach(actionGroup => {
	        // key = groupId, value = only visible actions
	        map[actionGroup.id] = actionGroup.actions.filter(a => a.visible);
	    });
	    console.log("configLoader.js -> getActionGroupMap: map=", map);
	    return map;
	},
	
	// -------------------------------------------
	// -------------------------------------------

	// ✅ Load all validator groups
	async getValidatorGroups() {
	    console.log("configLoader.js -> getValidatorGroups: called");
	    const data = await this.loadAll(); // data = json.data
	    const validatorGroups = data?.validators || [];
	    console.log("configLoader.js -> getValidatorGroups: validatorGroups=", validatorGroups);
	    return validatorGroups;
	},

	// ✅ Get one validator group by ID
	async getValidatorGroup(validatorGroupId) {
	    console.log("configLoader.js -> getValidatorGroup: validatorGroupId=", validatorGroupId);
	    const validatorGroups = await this.getValidatorGroups();
	    const validatorGroup = validatorGroups.find(g => g.id === validatorGroupId) || null;
	    console.log("configLoader.js -> getValidatorGroup: result=", validatorGroup);
	    return validatorGroup;
	},

	// ✅ Convert validator groups into a map: { groupId → validators[] }
	async getValidatorGroupMap() {
	    console.log("configLoader.js -> getValidatorGroupMap: called");
	    const validatorGroups = await this.getValidatorGroups();
	    const map = {};
	    validatorGroups.forEach(group => {
	        map[group.id] = group.validators; // keep full list
	    });
	    console.log("configLoader.js -> getValidatorGroupMap: map=", map);
	    return map;
	},

	// ✅ Optional: build regexConfig from validatorGroupsMap
	async buildValidatorRegexConfig(validatorGroupsMap) {
	    const regexConfig = {};
	    Object.values(validatorGroupsMap).forEach(group => {
	        group.forEach(v => {
	            regexConfig[v.type.toLowerCase()] = v.validRegexExpression;
	        });
	    });
	    console.log("configLoader.js -> buildValidatorRegexConfig: regexConfig=", regexConfig);
	    return regexConfig;
	}
};