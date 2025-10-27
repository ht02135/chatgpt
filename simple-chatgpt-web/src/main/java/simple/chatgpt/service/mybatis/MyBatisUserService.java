package simple.chatgpt.service.mybatis;

import java.util.List;

import simple.chatgpt.pojo.mybatis.MyBatisUserUser;

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

    /**
     * Get users with paging, sorting, and filtering
     * @param page page number (1-based)
     * @param size page size
     * @param sortField field to sort by
     * @param sortOrder ASC or DESC
     * @param firstName filter by first name
     * @param lastName filter by last name
     * @param email filter by email
     * @param addressLine1 filter by address line 1
     * @param addressLine2 filter by address line 2
     * @param city filter by city
     * @param state filter by state
     * @param country filter by country
     * @return list of filtered users
     */
    List<MyBatisUserUser> getUsersPagedFiltered(int page, int size, String sortField, String sortOrder,
        String firstName, String lastName, String email, String addressLine1, String addressLine2, String city, String state, String country);

    /**
     * Get total user count with filters
     * @param firstName filter by first name
     * @param lastName filter by last name
     * @param email filter by email
     * @param addressLine1 filter by address line 1
     * @param addressLine2 filter by address line 2
     * @param city filter by city
     * @param state filter by state
     * @param country filter by country
     * @return total number of filtered users
     */
    int getTotalUserCountFiltered(String firstName, String lastName, String email, String addressLine1, String addressLine2, String city, String state, String country);
}