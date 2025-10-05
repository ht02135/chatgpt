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

import simple.chatgpt.config.management.PageRoleGroupConfig;
import simple.chatgpt.config.management.RoleConfig;
import simple.chatgpt.config.management.RoleGroupConfig;
import simple.chatgpt.config.management.RoleRefConfig;

@Component
public class SecurityConfigLoader {

    private static final Logger logger = LogManager.getLogger(SecurityConfigLoader.class);

    private List<RoleConfig> roles = new ArrayList<>();
    private List<RoleGroupConfig> roleGroups = new ArrayList<>();
    private List<PageRoleGroupConfig> pageRoleGroups = new ArrayList<>();

    private static final String DEFAULT_CONFIG_FILE = "/config/management/security-config.xml";

    @PostConstruct
    private void init() {
        logger.debug("SecurityConfigLoader: init called");

        try (InputStream inputStream = SecurityConfigLoader.class.getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find security config: " + DEFAULT_CONFIG_FILE);
            }

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            // ===== Load roles =====
            NodeList roleNodes = document.getElementsByTagName("role");
            for (int i = 0; i < roleNodes.getLength(); i++) {
                Element e = (Element) roleNodes.item(i);
                String name = e.getAttribute("name");
                String desc = e.getAttribute("description");
                RoleConfig role = new RoleConfig(name, desc);
                roles.add(role);
                logger.debug("Loaded role: name={}, description={}", name, desc);
            }

            // ===== Load role-groups =====
            NodeList groupNodes = document.getElementsByTagName("role-group");
            for (int i = 0; i < groupNodes.getLength(); i++) {
                Element g = (Element) groupNodes.item(i);
                String groupName = g.getAttribute("name");
                RoleGroupConfig rg = new RoleGroupConfig(groupName, null);

                NodeList roleRefs = g.getElementsByTagName("role-ref");
                for (int j = 0; j < roleRefs.getLength(); j++) {
                    Element r = (Element) roleRefs.item(j);
                    rg.addRole(new RoleRefConfig(r.getAttribute("name")));
                }

                roleGroups.add(rg);
                logger.debug("Loaded role group: name={}, roles={}", groupName, rg.getRoles().size());
            }

            // ===== Load page-role-groups =====
            NodeList pageNodes = document.getElementsByTagName("page-role-group");
            for (int i = 0; i < pageNodes.getLength(); i++) {
                Element p = (Element) pageNodes.item(i);
                String urlPattern = p.getAttribute("url-pattern");
                String group = p.getAttribute("role-group");
                PageRoleGroupConfig prg = new PageRoleGroupConfig(urlPattern, group);
                pageRoleGroups.add(prg);
                logger.debug("Loaded page-role-group: urlPattern={}, roleGroup={}", urlPattern, group);
            }

        } catch (Exception e) {
            logger.error("Failed to load security configuration", e);
            throw new RuntimeException(e);
        }
    }

    public List<RoleConfig> getRoles() {
        return roles;
    }

    public List<RoleGroupConfig> getRoleGroups() {
        return roleGroups;
    }

    public List<PageRoleGroupConfig> getPageRoleGroups() {
        return pageRoleGroups;
    }
}
