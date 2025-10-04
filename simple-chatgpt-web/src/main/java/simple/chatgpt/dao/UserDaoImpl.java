package simple.chatgpt.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import simple.chatgpt.pojo.User;

@Repository
public class UserDaoImpl implements UserDao {
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User save(User user) {
        logger.debug("Saving/updating user: {}", user.getName());
        Session session = sessionFactory.getCurrentSession();

        if (user.getId() > 0) {
            // This is an update - use merge or update
            logger.debug("Updating existing user with ID: {}", user.getId());
            User mergedUser = (User) session.merge(user);
            return mergedUser;
        } else {
            // This is a new user - use save
            logger.debug("Creating new user: {}", user.getName());
            session.save(user);
            return user;
        }
    }

// Alternative implementation using saveOrUpdate (deprecated but still works)
/*
@Override
public User save(User user) {
    logger.debug("Saving/updating user: {}", user.getName());
    Session session = sessionFactory.getCurrentSession();
    session.saveOrUpdate(user);
    return user;
}
*/

    @Override
    public void delete(int id) {
        logger.debug("Deleting user with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        if (user != null) {
            session.delete(user);
        }
    }

    @Override
    public User get(int id) {
        logger.debug("Getting user with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public List<User> getAll() {
        logger.debug("Getting all users");
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM User", User.class).list();
    }
}
