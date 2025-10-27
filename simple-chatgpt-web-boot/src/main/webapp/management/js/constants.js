// constants.js

//------------------------

// Compute context path
const API_CONTEXT_PATH_CONST = "/" + window.location.pathname.split("/")[1];
window.API_CONTEXT_PATH = API_CONTEXT_PATH_CONST; // Assign global first
console.debug("window.API_CONTEXT_PATH =", window.API_CONTEXT_PATH);

// Then attach batch downloads URL globally
window.API_BATCH_DOWNLOADS_REQUEST = window.API_CONTEXT_PATH + "/api/batch/downloads";
console.debug("window.API_BATCH_DOWNLOADS_REQUEST =", window.API_BATCH_DOWNLOADS_REQUEST);

//------------------------

const JSP_CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
console.debug("JSP_CONTEXT_PATH =", JSP_CONTEXT_PATH);

const KO_SCRIPT = JSP_CONTEXT_PATH + "/management/js/knockout-latest.js";
console.debug("KO_SCRIPT =", KO_SCRIPT);

const LOGIN_PAGE = JSP_CONTEXT_PATH + "/management/jsp/auth/login.jsp";
console.debug("LOGIN_PAGE =", LOGIN_PAGE);

const LOGOUT_PAGE = JSP_CONTEXT_PATH + "/management/jsp/auth/logout.jsp";
console.debug("LOGOUT_PAGE =", LOGOUT_PAGE);

const REGISTER_PAGE = JSP_CONTEXT_PATH + "/management/jsp/auth/register.jsp";
console.debug("REGISTER_PAGE =", REGISTER_PAGE);

const INDEX_PAGE = JSP_CONTEXT_PATH + "/index.jsp";
console.debug("INDEX_PAGE =", INDEX_PAGE);

const DASHBOARD_PAGE = JSP_CONTEXT_PATH + "/dashboard.jsp";
console.debug("DASHBOARD_PAGE =", DASHBOARD_PAGE);

const API_AUTH_LOGIN = JSP_CONTEXT_PATH + "/api/management/auth/login";
console.debug("API_AUTH_LOGIN =", API_AUTH_LOGIN);

const API_AUTH_LOGOUT = JSP_CONTEXT_PATH + "/api/management/auth/logout";
console.debug("API_AUTH_LOGOUT =", API_AUTH_LOGOUT);

const API_AUTH_REGISTER = JSP_CONTEXT_PATH + "/api/management/auth/register";
console.debug("API_AUTH_REGISTER =", API_AUTH_REGISTER);

const API_AUTH_VALIDATE = JSP_CONTEXT_PATH + "/api/management/auth/validate";
console.debug("API_AUTH_VALIDATE =", API_AUTH_VALIDATE);

// API
const API_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
console.debug("API_CONTEXT_PATH =", API_CONTEXT_PATH);

const API_CONFIG = API_CONTEXT_PATH + "/api/management/config";
console.debug("API_CONFIG =", API_CONFIG);

const API_PROPERTY = API_CONTEXT_PATH + "/api/management/properties";
console.debug("API_PROPERTY =", API_PROPERTY);

const API_USER = API_CONTEXT_PATH + "/api/management/users";
console.debug("API_USER =", API_USER);

const API_JOB_REQUEST = API_CONTEXT_PATH + "/api/batch/jobRequests";
console.debug("API_JOB_REQUEST =", API_JOB_REQUEST);

const API_BATCH_REQUEST = API_CONTEXT_PATH + "/api/batch";
console.debug("API_BATCH_REQUEST =", API_BATCH_REQUEST);

const API_BATCH_DOWNLOADS_REQUEST = API_CONTEXT_PATH + "/api/batch/downloads";
console.debug("API_BATCH_DOWNLOADS_REQUEST =", API_BATCH_DOWNLOADS_REQUEST);

const API_PAGE = API_CONTEXT_PATH + "/api/management/pages";
console.debug("API_PAGE =", API_PAGE);

const API_USERLIST = API_CONTEXT_PATH + "/api/management/userlists";
console.debug("API_USERLIST =", API_USERLIST);

const API_USERLIST_MEMBER = API_CONTEXT_PATH + "/api/management/userlistmembers";
console.debug("API_USERLIST_MEMBER =", API_USERLIST_MEMBER);

const API_ROLE = API_CONTEXT_PATH + "/api/management/roles";
console.debug("API_ROLE =", API_ROLE);

const API_ROLE_GROUP = API_CONTEXT_PATH + "/api/management/rolegroups";
console.debug("API_ROLE_GROUP =", API_ROLE_GROUP);

const API_ROLE_GROUP_ROLE = API_CONTEXT_PATH + "/api/management/rolegrouprolemappings";
console.debug("API_ROLE_GROUP_ROLE =", API_ROLE_GROUP_ROLE);

const API_PAGE_ROLE_GROUP = API_CONTEXT_PATH + "/api/management/pagerolegroups";
console.debug("API_PAGE_ROLE_GROUP =", API_PAGE_ROLE_GROUP);

const API_USER_ROLE_GROUP = API_CONTEXT_PATH + "/api/management/userrolegroups";
console.debug("API_USER_ROLE_GROUP =", API_USER_ROLE_GROUP);

const API_OPENAI_AGENT_CREW = API_CONTEXT_PATH + "/api/openai/agentcrew";
console.debug("API_OPENAI_AGENT_CREW =", API_OPENAI_AGENT_CREW);
