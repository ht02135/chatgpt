package simple.chatgpt.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.chatgpt.dao.UserDao;
import simple.chatgpt.pojo.User;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User save(User user) {
        logger.debug("Saving user: {}", user.getName());
        return userDao.save(user); // Now returns the saved User
    }

    @Override
    public void delete(int id) {
        logger.debug("Deleting user with ID: {}", id);
        userDao.delete(id);
    }

    @Override
    public User get(int id) {
        logger.debug("Getting user with ID: {}", id);
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() {
        logger.debug("Getting all users");
        return userDao.getAll();
    }
}
