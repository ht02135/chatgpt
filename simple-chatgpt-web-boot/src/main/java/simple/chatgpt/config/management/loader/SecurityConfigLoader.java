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

    private static final String DEFAULT_CONFIG_FILE = "/config/management/security-config.xml";

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
            NodeList roleNodes = document.getElementsByTagName("role");
            for (int i = 0; i < roleNodes.getLength(); i++) {
                Element e = (Element) roleNodes.item(i);
                String name = e.getAttribute("name");
                String desc = e.getAttribute("description");

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
            NodeList groupNodes = document.getElementsByTagName("role-group");
            for (int i = 0; i < groupNodes.getLength(); i++) {
                Element g = (Element) groupNodes.item(i);
                String groupName = g.getAttribute("name");
                String delimitRoles = g.getAttribute("delimitRoles");

                RoleGroupConfig rg = new RoleGroupConfig(groupName, groupName, delimitRoles);
                NodeList roleRefs = g.getElementsByTagName("role-ref");
                for (int j = 0; j < roleRefs.getLength(); j++) {
                    Element r = (Element) roleRefs.item(j);
                    RoleRefConfig ref = new RoleRefConfig(r.getAttribute("name"));
                    rg.addRole(ref);
                    logger.debug("Added role-ref name={}", r.getAttribute("name"));
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
            NodeList pageNodes = document.getElementsByTagName("page-role-group");
            for (int i = 0; i < pageNodes.getLength(); i++) {
                Element p = (Element) pageNodes.item(i);
                String urlPattern = p.getAttribute("url-pattern");
                String groupRef = p.getAttribute("role-group-ref");
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
            NodeList pagesNodes = document.getElementsByTagName("page");
            for (int i = 0; i < pagesNodes.getLength(); i++) {
                Element p = (Element) pagesNodes.item(i);
                String urlPattern = p.getAttribute("url-pattern");
                String delimitRoleGroups = p.getAttribute("delimit-role-group");

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
            NodeList userNodes = document.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element u = (Element) userNodes.item(i);
                String roleGroupRef = u.getAttribute("role-group-ref");
                String delimitRoleGroups = u.getAttribute("delimit-role-group");

                UserConfig user = new UserConfig(
                        u.getAttribute("user_name"),
                        u.getAttribute("user_key"),
                        u.getAttribute("password"),
                        u.getAttribute("first_name"),
                        u.getAttribute("last_name"),
                        u.getAttribute("email"),
                        u.getAttribute("address_line_1"),
                        u.getAttribute("address_line_2"),
                        u.getAttribute("city"),
                        u.getAttribute("state"),
                        u.getAttribute("post_code"),
                        u.getAttribute("country"),
                        Boolean.parseBoolean(u.getAttribute("active")),
                        Boolean.parseBoolean(u.getAttribute("locked")),
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
