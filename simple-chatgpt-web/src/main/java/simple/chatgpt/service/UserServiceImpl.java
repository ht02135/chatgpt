package simple.chatgpt.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import simple.chatgpt.dao.UserDao;
import simple.chatgpt.pojo.User;
import simple.chatgpt.service.mybatis.PropertyService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;
    
    /*
    In Spring, beans like UserService, PropertyService, UserDao, etc., 
    are usually singletons. That means one instance is created at startup 
    and shared across all threads.
	Spring’s dependency injection happens once, during bean creation. After 
	that, Spring doesn’t reassign the field.
	//////////////
	even if i do @Autowired at here
	There’s no thread-safety issue.
	The reference won’t change after injection, so multiple threads won’t 
	see it “switching” to another instance.
    */
    @Autowired
    private PropertyService propertyService;

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
