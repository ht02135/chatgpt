package simple.chatgpt.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.chatgpt.dao.User;
import simple.chatgpt.pojo.User;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private User userDao;

    public void save(User user) {
        logger.debug("Saving user: {}", user.getName());
        userDao.save(user);
    }

    public void delete(int id) {
        logger.debug("Deleting user with ID: {}", id);
        userDao.delete(id);
    }

    public User get(int id) {
        logger.debug("Getting user with ID: {}", id);
        return userDao.get(id);
    }

    public List<User> getAll() {
        logger.debug("Getting all users");
        return userDao.getAll();
    }
}
