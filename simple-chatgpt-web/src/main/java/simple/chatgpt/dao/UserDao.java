package simple.chatgpt.dao;

import java.util.List;

import simple.chatgpt.pojo.User;

public interface UserDao {
    User save(User user); // Return the saved User
    void delete(int id);
    User get(int id);
    List<User> getAll();
}
