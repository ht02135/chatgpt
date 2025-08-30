package simple.chatgpt.service.mybatis;

import simple.chatgpt.pojo.User;
import java.util.List;

public interface UserService {

    /**
     * Save a user (insert if new, update if existing)
     * @param user the user to save
     * @return the saved user with generated ID if new
     */
    User save(User user);

    /**
     * Get user by ID
     * @param id the user ID
     * @return the user or null if not found
     */
    User get(int id);

    /**
     * Get all users
     * @return list of all users
     */
    List<User> getAll();

    /**
     * Delete user by ID
     * @param id the user ID to delete
     */
    void delete(int id);

    /**
     * Find user by email
     * @param email the email address
     * @return the user or null if not found
     */
    User findByEmail(String email);

    /**
     * Check if user exists by ID
     * @param id the user ID
     * @return true if user exists, false otherwise
     */
    boolean exists(int id);
}