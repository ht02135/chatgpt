package simple.chatgpt.service.management;

import java.util.Map;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.util.PagedResult;
import java.util.List;

public interface UserManagementService {

    // ======= 5 CORE METHODS (on top) =======
    UserManagementPojo create(UserManagementPojo user);
    UserManagementPojo update(Long id, UserManagementPojo user);
    PagedResult<UserManagementPojo> search(Map<String, String> params);
    UserManagementPojo get(Long id);
    void delete(Long id);
    
    // ======= OTHER METHODS =======
    
}
