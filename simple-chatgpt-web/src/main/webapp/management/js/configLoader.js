// configLoader.js

// const API_CONFIG = '/chatgpt/api/management/config';
// detect context path automatically (first part of URL path)
const CONFIG_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
// build API endpoint using context path
const API_CONFIG = `${CONFIG_CONTEXT_PATH}/api/management/config`;

let _cache = null;
let _cacheTimestamp = 0;
const CACHE_TTL = 5 * 60 * 1000; // 5 minutes

const configLoader = {
    // ---------------------------
    // Core fetch
    // ---------------------------
    async loadAll() {
        console.log("configLoader.js -> loadAll: called");
        try {
            const res = await fetch(API_CONFIG);
            const json = await res.json();
            if (json.status !== 'SUCCESS') throw new Error('Failed to load config');
            console.log("configLoader.js -> loadAll: json.data=", json.data);
            return json.data;
        } catch (err) {
            console.error("configLoader.js -> loadAll: ❌ Config load error:", err);
            return null;
        }
    },

    async loadAllCached() {
        const now = Date.now();
        if (_cache && (now - _cacheTimestamp) < CACHE_TTL) {
            console.log("configLoader.js -> loadAllCached: using cache");
            return _cache;
        }
        console.log("configLoader.js -> loadAllCached: fetching fresh data");
        const data = await this.loadAll();
        if (data) {
            _cache = data;
            _cacheTimestamp = now;
        }
        return data;
    },

    invalidateCache() {
        console.log("configLoader.js -> invalidateCache: cache cleared");
        _cache = null;
        _cacheTimestamp = 0;
    },

    // ---------------------------
    // Form / Grid / Regex Configs
    // ---------------------------
    async getFormConfig(formId) {
        const data = await this.loadAllCached();
        const result = data?.forms.find(f => f.id === formId) || null;
        console.log("configLoader.js -> getFormConfig: formId=", formId, "result=", result);
        return result;
    },

    async getGridConfig(gridId) {
        const data = await this.loadAllCached();
        const result = data?.grids.find(g => g.id === gridId) || null;
        console.log("configLoader.js -> getGridConfig: gridId=", gridId, "result=", result);
        return result;
    },

    async getRegexConfig() {
        const data = await this.loadAllCached();
        const result = data?.regex || {};
        console.log("configLoader.js -> getRegexConfig: result=", result);
        return result;
    },

    async getRegexMapConfig() {
        const regexArray = await this.getRegexConfig();
        const regexMap = {};
        regexArray.forEach(r => {
            regexMap[r.id] = r.expression;
        });
        console.log("configLoader.js -> getRegexMapConfig: regexMap=", regexMap);
        return regexMap;
    },

    // ---------------------------
    // Action Groups
    // ---------------------------
    async getActionGroups() {
        const data = await this.loadAllCached();
        const result = data?.actions || [];
        console.log("configLoader.js -> getActionGroups: result=", result);
        return result;
    },

    async getActionGroup(actionGroupId) {
        const actionGroups = await this.getActionGroups();
        const actionGroup = actionGroups.find(g => g.id === actionGroupId) || null;
        if (actionGroup) {
            actionGroup.actions = actionGroup.actions.filter(a => a.visible);
        }
        console.log("configLoader.js -> getActionGroup: actionGroupId=", actionGroupId, "result=", actionGroup);
        return actionGroup;
    },

    async getActionGroupMap() {
        const actionGroups = await this.getActionGroups();
        const map = {};
        actionGroups.forEach(group => {
            map[group.id] = group.actions.filter(a => a.visible);
        });
        console.log("configLoader.js -> getActionGroupMap: map=", map);
        return map;
    },

    // ---------------------------
    // Validator Groups
    // ---------------------------
    async getValidatorGroups() {
        const data = await this.loadAllCached();
        const validatorGroups = data?.validators || [];
        console.log("configLoader.js -> getValidatorGroups: validatorGroups=", validatorGroups);
        return validatorGroups;
    },

    async getValidatorGroup(validatorGroupId) {
        const validatorGroups = await this.getValidatorGroups();
        const validatorGroup = validatorGroups.find(g => g.id === validatorGroupId) || null;
        console.log("configLoader.js -> getValidatorGroup: validatorGroupId=", validatorGroupId, "result=", validatorGroup);
        return validatorGroup;
    },

    async getValidatorGroupMap() {
        const validatorGroups = await this.getValidatorGroups();
        const map = {};
        validatorGroups.forEach(group => {
            map[group.id] = group.validators;
        });
        console.log("configLoader.js -> getValidatorGroupMap: map=", map);
        return map;
    },

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

export default configLoader;
