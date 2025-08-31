package simple.chatgpt.service.mybatis;

import simple.chatgpt.pojo.mybatis.MyBatisUserUser;
import java.util.List;

public interface MyBatisUserService {

    /**
     * Save a user (insert if new, update if existing)
     * @param user the user to save
     * @return the saved user with generated ID if new
     */
    MyBatisUserUser save(MyBatisUserUser user);

    /**
     * Get user by ID
     * @param id the user ID
     * @return the user or null if not found
     */
    MyBatisUserUser get(int id);

    /**
     * Get all users
     * @return list of all users
     */
    List<MyBatisUserUser> getAll();

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
    MyBatisUserUser findByEmail(String email);

    /**
     * Check if user exists by ID
     * @param id the user ID
     * @return true if user exists, false otherwise
     */
    boolean exists(int id);

    /**
     * Get users with paging and sorting
     * @param page page number (1-based)
     * @param size page size
     * @param sortField field to sort by
     * @param sortOrder ASC or DESC
     * @return list of users
     */
    List<MyBatisUserUser> getUsersPaged(int page, int size, String sortField, String sortOrder);

    /**
     * Get total user count
     * @return total number of users
     */
    int getTotalUserCount();
}