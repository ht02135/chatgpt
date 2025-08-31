package simple.chatgpt.mapper;

import org.apache.ibatis.annotations.*;
import simple.chatgpt.pojo.mybatis.MyBatisUserUser;
import java.util.List;

public interface UserMapper {

    // ============== ANNOTATION-BASED METHODS (Simple queries) ==============

    /**
     * Insert a new user - Using annotation
     * @param user the user to insert
     * @return number of affected rows
     */
    @Insert("INSERT INTO users (name, email, first_name, last_name, password, address_line_1, address_line_2, city, state, post_code, country) VALUES (#{name}, #{email}, #{firstName}, #{lastName}, #{password}, #{addressLine1}, #{addressLine2}, #{city}, #{state}, #{postCode}, #{country})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(MyBatisUserUser user);

    /**
     * Update an existing user - Using annotation
     * @param user the user to update
     * @return number of affected rows
     */
    @Update("UPDATE users SET name = #{name}, email = #{email}, first_name = #{firstName}, last_name = #{lastName}, password = #{password}, address_line_1 = #{addressLine1}, address_line_2 = #{addressLine2}, city = #{city}, state = #{state}, post_code = #{postCode}, country = #{country} WHERE id = #{id}")
    int update(MyBatisUserUser user);

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
    @Select("SELECT id, name, email, first_name, last_name, password, address_line_1, address_line_2, city, state, post_code, country FROM users WHERE id = #{id}")
    MyBatisUserUser selectById(@Param("id") int id);

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
    List<MyBatisUserUser> selectAll();

    /**
     * Find user by email - Defined in XML
     * @param email the email address
     * @return the user or null if not found
     */
    MyBatisUserUser selectByEmail(@Param("email") String email);

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
    List<MyBatisUserUser> selectByNameLike(@Param("name") String name);

    /**
     * Find users with pagination - Defined in XML for dynamic pagination
     * @param offset starting position
     * @param limit number of records
     * @return list of users
     */
    List<MyBatisUserUser> selectWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * Find users with pagination and sorting - Defined in XML for dynamic pagination/sorting
     * @param offset starting position
     * @param limit number of records
     * @param sortField field to sort by
     * @param sortOrder ASC or DESC
     * @return list of users
     */
    List<MyBatisUserUser> selectWithPagingAndSorting(@Param("offset") int offset, @Param("limit") int limit, @Param("sortField") String sortField, @Param("sortOrder") String sortOrder);

    /**
     * Dynamic search with filters - Defined in XML for complex conditions
     * @param user search criteria
     * @return list of matching users
     */
    List<MyBatisUserUser> selectByDynamicCriteria(MyBatisUserUser user);

    /**
     * Find users with paging, sorting, and filtering - Defined in XML for dynamic search
     */
    List<MyBatisUserUser> selectWithPagingSortingFiltering(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("sortField") String sortField,
        @Param("sortOrder") String sortOrder,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("addressLine1") String addressLine1,
        @Param("addressLine2") String addressLine2,
        @Param("city") String city,
        @Param("state") String state,
        @Param("country") String country
    );

    /**
     * Count users with filtering - Defined in XML for dynamic search
     */
    int countWithFiltering(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("addressLine1") String addressLine1,
        @Param("addressLine2") String addressLine2,
        @Param("city") String city,
        @Param("state") String state,
        @Param("country") String country
    );
}