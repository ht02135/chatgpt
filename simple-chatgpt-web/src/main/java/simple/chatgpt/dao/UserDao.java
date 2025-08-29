package simple.chatgpt.dao;

import simple.chatgpt.pojo.User;
import java.util.List;

public interface UserDao {
    User save(User user); // Return the saved User
    void delete(int id);
    User get(int id);
    List<User> getAll();
}
