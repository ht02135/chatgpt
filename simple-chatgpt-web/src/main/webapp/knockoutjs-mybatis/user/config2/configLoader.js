// configLoader.js

const API_BASE = '/chatgpt/api/mybatis/config';

async function loadConfig() {
    try {
        const res = await fetch(`${API_BASE}/all`);
        const json = await res.json();
        if (json.status !== 'SUCCESS') throw new Error('Failed to load config');
        return json.data;
    } catch (err) {
        console.error("❌ Config load error:", err);
        return null;
    }
}
