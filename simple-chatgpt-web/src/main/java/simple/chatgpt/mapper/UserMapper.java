package simple.chatgpt.mapper;

import org.apache.ibatis.annotations.*;
import simple.chatgpt.pojo.User;
import java.util.List;

public interface UserMapper {

    // ============== ANNOTATION-BASED METHODS (Simple queries) ==============

    /**
     * Insert a new user - Using annotation
     * @param user the user to insert
     * @return number of affected rows
     */
    @Insert("INSERT INTO users (name, email) VALUES (#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(User user);

    /**
     * Update an existing user - Using annotation
     * @param user the user to update
     * @return number of affected rows
     */
    @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
    int update(User user);

    /**
     * Delete user by ID - Using annotation
     * @param id the user ID
     * @return number of affected rows
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") int id);

    /**
     * Find user by ID - Using annotation
     * @param id the user ID
     * @return the user or null if not found
     */
    @Select("SELECT id, name, email FROM users WHERE id = #{id}")
    User selectById(@Param("id") int id);

    /**
     * Count users by ID - Using annotation
     * @param id the user ID
     * @return count (0 or 1)
     */
    @Select("SELECT COUNT(*) FROM users WHERE id = #{id}")
    int countById(@Param("id") int id);

    // ============== XML-BASED METHODS (Complex queries) ==============

    /**
     * Find all users - Defined in XML for complex sorting/filtering
     * @return list of all users
     */
    List<User> selectAll();

    /**
     * Find user by email - Defined in XML
     * @param email the email address
     * @return the user or null if not found
     */
    User selectByEmail(@Param("email") String email);

    /**
     * Count all users - Defined in XML
     * @return total number of users
     */
    int countAll();

    /**
     * Check if email exists - Defined in XML
     * @param email the email address
     * @return count (0 or 1)
     */
    int countByEmail(@Param("email") String email);

    /**
     * Find users by name pattern - Defined in XML for dynamic search
     * @param name the name pattern to search
     * @return list of matching users
     */
    List<User> selectByNameLike(@Param("name") String name);

    /**
     * Find users with pagination - Defined in XML for dynamic pagination
     * @param offset starting position
     * @param limit number of records
     * @return list of users
     */
    List<User> selectWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * Find users with pagination and sorting - Defined in XML for dynamic pagination/sorting
     * @param offset starting position
     * @param limit number of records
     * @param sortField field to sort by
     * @param sortOrder ASC or DESC
     * @return list of users
     */
    List<User> selectWithPagingAndSorting(@Param("offset") int offset, @Param("limit") int limit, @Param("sortField") String sortField, @Param("sortOrder") String sortOrder);

    /**
     * Dynamic search with filters - Defined in XML for complex conditions
     * @param user search criteria
     * @return list of matching users
     */
    List<User> selectByDynamicCriteria(User user);
}