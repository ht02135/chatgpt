package simple.chatgpt.service.mybatis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.chatgpt.mapper.UserMapper;
import simple.chatgpt.pojo.User;

import java.util.List;

@Service("mybatisUserService")
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public User save(User user) {
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
    public User get(int id) {
        logger.debug("MyBatis - Getting user by ID: {}", id);
        User user = userMapper.selectById(id);
        if (user != null) {
            logger.debug("MyBatis - Found user: {}", user.getName());
        } else {
            logger.debug("MyBatis - No user found with ID: {}", id);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        logger.debug("MyBatis - Getting all users");
        List<User> users = userMapper.selectAll();
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
    public User findByEmail(String email) {
        logger.debug("MyBatis - Finding user by email: {}", email);
        User user = userMapper.selectByEmail(email);
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
}