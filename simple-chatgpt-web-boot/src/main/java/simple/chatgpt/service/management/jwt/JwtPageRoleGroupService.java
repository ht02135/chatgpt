package simple.chatgpt.service.management.jwt;

import java.util.List;

public interface JwtPageRoleGroupService {

    /**
     * Return the list of role-group names allowed to access a specific URL.
     *
     * @param url the URL to check
     * @return list of allowed role-group names
     */
    List<String> getAllowedRoleGroups(String url);
    List<String> getAllowedRoles(String url);
    
    List<String> getAllowedRoleGroupNames(String url);
    List<String> getAllowedRoleNames(String url);
}
