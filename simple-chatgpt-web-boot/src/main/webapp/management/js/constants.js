
// JSP
const JSP_CONTEXT_PATH = window.location.origin + "/" + window.location.pathname.split("/")[1];
const KO_SCRIPT = JSP_CONTEXT_PATH + "/management/js/knockout-latest.js";
const LOGIN_PAGE = JSP_CONTEXT_PATH + "/management/jsp/auth/login.jsp";
const LOGOUT_PAGE = JSP_CONTEXT_PATH + "/management/jsp/auth/logout.jsp";
const REGISTER_PAGE = JSP_CONTEXT_PATH + "/management/jsp/auth/register.jsp";
const INDEX_PAGE = JSP_CONTEXT_PATH + "/index.jsp";
const DASHBOARD_PAGE = JSP_CONTEXT_PATH + "/dashboard.jsp";
const API_AUTH_LOGIN = JSP_CONTEXT_PATH + "/api/management/auth/login";
const API_AUTH_REGISTER = JSP_CONTEXT_PATH + "/api/management/auth/register";

// API
const API_CONTEXT_PATH = "/" + window.location.pathname.split("/")[1];
const API_CONFIG = API_CONTEXT_PATH + "/api/management/config";
const API_PROPERTY = API_CONTEXT_PATH + "/api/management/properties";
const API_USER = API_CONTEXT_PATH + "/api/management/users";
const API_USERLIST = API_CONTEXT_PATH + "/api/management/userlists";
const API_USERLIST_MEMBER = API_CONTEXT_PATH + "/api/management/userlistmembers";
const API_ROLE = API_CONTEXT_PATH + "/api/management/roles";
const API_ROLE_GROUP = API_CONTEXT_PATH + "/api/management/rolegroups";
const API_ROLE_GROUP_ROLE = API_CONTEXT_PATH + "/api/management/rolegrouprolemappings";
const API_PAGE_ROLE_GROUP = API_CONTEXT_PATH + "/api/management/pagerolegroups";
const API_USER_ROLE_GROUP = API_CONTEXT_PATH + "/api/management/userrolegroups";

