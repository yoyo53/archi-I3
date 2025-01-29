package user.service.userservice.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import user.service.userservice.model.User;
import user.service.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.created(null).body(createdUser);
        } catch (Exception e) {
            logger.error("User creation failed", e);
            return ResponseEntity.badRequest().body("User creation failed");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody User user) {
        try{
            Long userId = userService.loginUser(user);
            return ResponseEntity.ok(userId);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("User login failed");
        }
    }
}