package simple.chatgpt.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import simple.chatgpt.pojo.User;
import simple.chatgpt.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public @ResponseBody User save(@RequestBody User user) {
        logger.debug("Received save request for user: {}", user.getName());
        userService.save(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public @ResponseBody void delete(@PathVariable int id) {
        logger.debug("Received delete request for user ID: {}", id);
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public @ResponseBody User get(@PathVariable int id) {
        logger.debug("Received get request for user ID: {}", id);
        return userService.get(id);
    }

    @GetMapping
    public @ResponseBody List<User> getAll() {
        logger.debug("Received get all users request");
        return userService.getAll();
    }
}
