package simple.chatgpt.config.management.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simple.chatgpt.config.management.security.PageConfig;
import simple.chatgpt.config.management.security.PageRoleGroupConfig;
import simple.chatgpt.config.management.security.RoleConfig;
import simple.chatgpt.config.management.security.RoleGroupConfig;
import simple.chatgpt.config.management.security.RoleRefConfig;
import simple.chatgpt.config.management.security.UserConfig;

@Component
public class SecurityConfigLoader {

    private static final Logger logger = LogManager.getLogger(SecurityConfigLoader.class);

    private List<RoleConfig> roles = new ArrayList<>();
    private List<RoleGroupConfig> roleGroups = new ArrayList<>();
    private List<PageRoleGroupConfig> pageRoleGroups = new ArrayList<>();
    private List<UserConfig> users = new ArrayList<>();
    private List<PageConfig> pages = new ArrayList<>();

    // =========================
    // Constants
    // =========================
    private static final String DEFAULT_CONFIG_FILE = "/config/management/security-config.xml";

    // Role attributes
    private static final String TAG_ROLE = "role";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_DESCRIPTION = "description";
    
    private static final String TAG_ROLE_GROUP = "role-group";
    private static final String TAG_PAGE_ROLE_GROUP = "page-role-group";
    
    private static final String TAG_ROLE_REF = "role-ref";
    private static final String ATTR_ROLE_GROUP_REF = "role-group-ref";
    
    private static final String TAG_USER = "user";
    private static final String TAG_PAGE = "page";
    private static final String ATTR_URL_PATTERN = "url-pattern";

    // delimit attributes
    private static final String ATTR_DELIMIT_ROLES = "delimit-roles";
    private static final String ATTR_DELIMIT_ROLE_GROUPS = "delimit-role-groups";

    // User attributes
    private static final String ATTR_USER_NAME = "user_name";
    private static final String ATTR_USER_KEY = "user_key";
    private static final String ATTR_PASSWORD = "password";
    private static final String ATTR_FIRST_NAME = "first_name";
    private static final String ATTR_LAST_NAME = "last_name";
    private static final String ATTR_EMAIL = "email";
    private static final String ATTR_ADDRESS_LINE_1 = "address_line_1";
    private static final String ATTR_ADDRESS_LINE_2 = "address_line_2";
    private static final String ATTR_CITY = "city";
    private static final String ATTR_STATE = "state";
    private static final String ATTR_POST_CODE = "post_code";
    private static final String ATTR_COUNTRY = "country";
    private static final String ATTR_ACTIVE = "active";
    private static final String ATTR_LOCKED = "locked";

    @PostConstruct
    private void init() {
        logger.debug("init called");

        try (InputStream inputStream = SecurityConfigLoader.class.getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find security config: " + DEFAULT_CONFIG_FILE);
            }

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            // =========================
            // Load Roles
            // =========================
            logger.debug("init Load roles ##############");
            NodeList roleNodes = document.getElementsByTagName(TAG_ROLE);
            for (int i = 0; i < roleNodes.getLength(); i++) {
                Element e = (Element) roleNodes.item(i);
                String name = e.getAttribute(ATTR_NAME);
                String desc = e.getAttribute(ATTR_DESCRIPTION);

                RoleConfig role = new RoleConfig(name, desc);
                roles.add(role);

                logger.debug("Loaded role name={}", name);
                logger.debug("Loaded role description={}", desc);
            }
            logger.debug("init Load roles DONE ##############");

            // =========================
            // Load Role Groups
            // =========================
            logger.debug("init Load role-groups ##############");
            NodeList groupNodes = document.getElementsByTagName(TAG_ROLE_GROUP);
            for (int i = 0; i < groupNodes.getLength(); i++) {
                Element g = (Element) groupNodes.item(i);
                String groupName = g.getAttribute(ATTR_NAME);
                String delimitRoles = g.getAttribute(ATTR_DELIMIT_ROLES);

                RoleGroupConfig rg = new RoleGroupConfig(groupName, groupName, delimitRoles);
                NodeList roleRefs = g.getElementsByTagName(TAG_ROLE_REF);
                for (int j = 0; j < roleRefs.getLength(); j++) {
                    Element r = (Element) roleRefs.item(j);
                    RoleRefConfig ref = new RoleRefConfig(r.getAttribute(ATTR_NAME));
                    rg.addRole(ref);
                    logger.debug("Added role-ref name={}", r.getAttribute(ATTR_NAME));
                }

                roleGroups.add(rg);
                logger.debug("Loaded role group name={}", groupName);
                logger.debug("Role group roles size={}", rg.getRoles().size());
            }
            logger.debug("init Load role-groups DONE ##############");

            // =========================
            // Load Page Role Groups
            // =========================
            logger.debug("init Load page-role-groups ##############");
            NodeList pageNodes = document.getElementsByTagName(TAG_PAGE_ROLE_GROUP);
            for (int i = 0; i < pageNodes.getLength(); i++) {
                Element p = (Element) pageNodes.item(i);
                String urlPattern = p.getAttribute(ATTR_URL_PATTERN);
                String groupRef = p.getAttribute(ATTR_ROLE_GROUP_REF);
                PageRoleGroupConfig prg = new PageRoleGroupConfig(urlPattern, groupRef);
                pageRoleGroups.add(prg);

                logger.debug("Loaded page-role-group urlPattern={}", urlPattern);
                logger.debug("Loaded page-role-group roleGroupRef={}", groupRef);
            }
            logger.debug("init Load page-role-groups DONE ##############");

            // =========================
            // Load Pages
            // =========================
            logger.debug("init Load pages ##############");
            NodeList pagesNodes = document.getElementsByTagName(TAG_PAGE);
            for (int i = 0; i < pagesNodes.getLength(); i++) {
                Element p = (Element) pagesNodes.item(i);
                String urlPattern = p.getAttribute(ATTR_URL_PATTERN);
                String delimitRoleGroups = p.getAttribute(ATTR_DELIMIT_ROLE_GROUPS);

                PageConfig page = new PageConfig(urlPattern, delimitRoleGroups);
                pages.add(page);

                logger.debug("Loaded page urlPattern={}", urlPattern);
                logger.debug("Loaded page delimitRoleGroups={}", delimitRoleGroups);
            }
            logger.debug("init Load pages DONE ##############");

            // =========================
            // Load Users
            // =========================
            logger.debug("init Load users ##############");
            NodeList userNodes = document.getElementsByTagName(TAG_USER);
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element u = (Element) userNodes.item(i);
                String roleGroupRef = u.getAttribute(ATTR_ROLE_GROUP_REF);
                String delimitRoleGroups = u.getAttribute(ATTR_DELIMIT_ROLE_GROUPS);

                UserConfig user = new UserConfig(
                        u.getAttribute(ATTR_USER_NAME),
                        u.getAttribute(ATTR_USER_KEY),
                        u.getAttribute(ATTR_PASSWORD),
                        u.getAttribute(ATTR_FIRST_NAME),
                        u.getAttribute(ATTR_LAST_NAME),
                        u.getAttribute(ATTR_EMAIL),
                        u.getAttribute(ATTR_ADDRESS_LINE_1),
                        u.getAttribute(ATTR_ADDRESS_LINE_2),
                        u.getAttribute(ATTR_CITY),
                        u.getAttribute(ATTR_STATE),
                        u.getAttribute(ATTR_POST_CODE),
                        u.getAttribute(ATTR_COUNTRY),
                        Boolean.parseBoolean(u.getAttribute(ATTR_ACTIVE)),
                        Boolean.parseBoolean(u.getAttribute(ATTR_LOCKED)),
                        roleGroupRef,
                        delimitRoleGroups
                );

                users.add(user);
                logger.debug("Loaded user userName={}", user.getUserName());
                logger.debug("Loaded user roleGroupRef={}", roleGroupRef);
                logger.debug("Loaded user delimitRoleGroups={}", delimitRoleGroups);
            }
            logger.debug("init Load users DONE ##############");

        } catch (Exception e) {
            logger.error("Failed to load security configuration", e);
            throw new RuntimeException(e);
        }

        logger.debug("init DONE!!!");
    }

    // ---------------- GETTERS ----------------
    public List<RoleConfig> getRoles() {
        logger.debug("getRoles called roles={}", roles);
        return roles;
    }

    public List<RoleGroupConfig> getRoleGroups() {
        logger.debug("getRoleGroups called roleGroups={}", roleGroups);
        return roleGroups;
    }

    public List<PageRoleGroupConfig> getPageRoleGroups() {
        logger.debug("getPageRoleGroups called pageRoleGroups={}", pageRoleGroups);
        return pageRoleGroups;
    }

    public List<PageConfig> getPages() {
        logger.debug("getPages called pages={}", pages);
        return pages;
    }

    public List<UserConfig> getUsers() {
        logger.debug("getUsers called users={}", users);
        return users;
    }
}
