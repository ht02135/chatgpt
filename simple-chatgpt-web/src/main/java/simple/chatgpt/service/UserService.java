package simple.chatgpt.service;

import java.util.List;

import simple.chatgpt.pojo.User;

public interface UserService {
    User save(User user);
    void delete(int id);
    User get(int id);
    List<User> getAll();
}
