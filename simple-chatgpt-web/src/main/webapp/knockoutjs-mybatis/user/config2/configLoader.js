// configLoader.js
const API_BASE = '/chatgpt/api/mybatis/config';

async function loadConfig() {
    const res = await fetch(`${API_BASE}/all`);
    const json = await res.json();
    if (json.status !== 'SUCCESS') throw new Error('Failed to load config');
    const data = json.data;

    // Convert arrays to maps for easy lookup
    const formsMap = {};
    data.forms.forEach(f => { formsMap[f.id] = f; });

    const gridsMap = {};
    data.grids.forEach(g => { gridsMap[g.id] = g; });

    const regexMap = {};
    data.regex.forEach(r => { regexMap[r.id] = r; });

    return { forms: formsMap, grids: gridsMap, regex: regexMap };
}
