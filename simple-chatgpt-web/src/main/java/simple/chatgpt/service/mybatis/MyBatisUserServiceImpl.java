package simple.chatgpt.service.mybatis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.chatgpt.mapper.UserMapper;
import simple.chatgpt.pojo.mybatis.MyBatisUserUser;

import java.util.List;

@Service("mybatisUserService")
@Transactional
public class MyBatisUserServiceImpl implements MyBatisUserService {
    private static final Logger logger = LogManager.getLogger(MyBatisUserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public MyBatisUserUser save(MyBatisUserUser user) {
        logger.debug("MyBatis - Saving user: {}", user.getName());

        if (user.getId() > 0) {
            // Update existing user
            int result = userMapper.update(user);
            if (result > 0) {
                logger.debug("MyBatis - User updated successfully: {}", user.getId());
                return userMapper.selectById(user.getId());
            } else {
                logger.warn("MyBatis - Failed to update user with ID: {}", user.getId());
                throw new RuntimeException("Failed to update user");
            }
        } else {
            // Insert new user
            int result = userMapper.insert(user);
            if (result > 0) {
                logger.debug("MyBatis - User created successfully with ID: {}", user.getId());
                return user; // MyBatis will populate the generated ID
            } else {
                logger.warn("MyBatis - Failed to create user");
                throw new RuntimeException("Failed to create user");
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MyBatisUserUser get(int id) {
        logger.debug("MyBatis - Getting user by ID: {}", id);
        MyBatisUserUser user = userMapper.selectById(id);
        if (user != null) {
            //logger.debug("MyBatis - Found user: {}", user.getName());
        	logger.debug("MyBatis - Found user: {}", user);
        } else {
            logger.debug("MyBatis - No user found with ID: {}", id);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyBatisUserUser> getAll() {
        logger.debug("MyBatis - Getting all users");
        List<MyBatisUserUser> users = userMapper.selectAll();
        logger.debug("MyBatis - Found {} users", users.size());
        return users;
    }

    @Override
    public void delete(int id) {
        logger.debug("MyBatis - Deleting user with ID: {}", id);
        int result = userMapper.deleteById(id);
        if (result > 0) {
            logger.debug("MyBatis - User deleted successfully: {}", id);
        } else {
            logger.warn("MyBatis - No user found to delete with ID: {}", id);
            throw new RuntimeException("User not found for deletion");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MyBatisUserUser findByEmail(String email) {
        logger.debug("MyBatis - Finding user by email: {}", email);
        MyBatisUserUser user = userMapper.selectByEmail(email);
        if (user != null) {
            logger.debug("MyBatis - Found user by email: {}", user.getName());
        } else {
            logger.debug("MyBatis - No user found with email: {}", email);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(int id) {
        logger.debug("MyBatis - Checking if user exists with ID: {}", id);
        int count = userMapper.countById(id);
        boolean exists = count > 0;
        logger.debug("MyBatis - User exists: {}", exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyBatisUserUser> getUsersPaged(int page, int size, String sortField, String sortOrder) {
        logger.debug("MyBatis - Getting users paged: page={}, size={}, sortField={}, sortOrder={}", page, size, sortField, sortOrder);
        int offset = (page - 1) * size;
        // Validate sortField and sortOrder to prevent SQL injection
        if (sortField == null || !(sortField.equals("id") || sortField.equals("name") || sortField.equals("email"))) {
            sortField = "id";
        }
        if (sortOrder == null || !(sortOrder.equalsIgnoreCase("ASC") || sortOrder.equalsIgnoreCase("DESC"))) {
            sortOrder = "ASC";
        }
        return userMapper.selectWithPagingAndSorting(offset, size, sortField, sortOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalUserCount() {
        logger.debug("MyBatis - Getting total user count");
        return userMapper.countAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyBatisUserUser> getUsersPagedFiltered(int page, int size, String sortField, String sortOrder,
        String firstName, String lastName, String email, String addressLine1, String addressLine2, String city, String state, String country) {
        logger.debug("MyBatis - Getting users paged/filtered: page={}, size={}, sortField={}, sortOrder={}, filters...", page, size, sortField, sortOrder);
        int offset = (page - 1) * size;
        return userMapper.selectWithPagingSortingFiltering(offset, size, sortField, sortOrder,
            firstName, lastName, email, addressLine1, addressLine2, city, state, country);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalUserCountFiltered(String firstName, String lastName, String email, String addressLine1, String addressLine2, String city, String state, String country) {
        return userMapper.countWithFiltering(firstName, lastName, email, addressLine1, addressLine2, city, state, country);
    }
}