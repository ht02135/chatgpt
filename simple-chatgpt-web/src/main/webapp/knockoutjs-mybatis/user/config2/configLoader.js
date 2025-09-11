// /simple-chatgpt-web/src/main/webapp/knockoutjs-mybatis/user/config2/configLoader.js

const API_BASE = '/chatgpt/api/mybatis/config';

const configLoader = {
    async loadAll() {
        try {
            const res = await fetch(`${API_BASE}/all`);
            const json = await res.json();
            if (json.status !== 'SUCCESS') throw new Error('Failed to load config');
            return json.data;
        } catch (err) {
            console.error("❌ Config load error:", err);
            return null;
        }
    },

    async getFormConfig(formId) {
        const data = await this.loadAll();
        return data?.forms.find(f => f.id === formId) || null;
    },

    async getGridConfig(gridId) {
        const data = await this.loadAll();
        return data?.grids.find(g => g.id === gridId) || null;
    },

    async getRegexConfig() {
        const data = await this.loadAll();
        return data?.regex || {};
    }
};
