package simple.chatgpt.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import simple.chatgpt.pojo.User;

import java.util.List;

@Repository
public class UserDao implements User {
    private static final Logger logger = LogManager.getLogger(UserDao.class);

    @Autowired
    private SessionFactory sessionFactory;

    public void save(User user) {
        logger.debug("Saving user: {}", user.getName());
        Session session = sessionFactory.getCurrentSession();
        session.save(user);
    }

    public void delete(int id) {
        logger.debug("Deleting user with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, id);
        if (user != null) {
            session.delete(user);
        }
    }

    public User get(int id) {
        logger.debug("Getting user with ID: {}", id);
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<User> getAll() {
        logger.debug("Getting all users");
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM User").list();
    }
}
