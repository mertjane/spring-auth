package auth.auth_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import auth.auth_api.model.UserModel;
import auth.auth_api.service.AuthService;

@RestController
@RequestMapping("/api/v1/authorize")
public class AuthController {

    @Autowired
    private AuthService authService;

    // GET method to retrieve a user by userId
    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long userId) {
        UserModel user = authService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user); // Return 200 OK with the user data
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if user does not exist
        }
    }

    // Create a new user
    @PostMapping("/signup")
    public ResponseEntity<UserModel> addUser(@RequestBody UserModel user) {
        UserModel savedUser = authService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Verify a user's email
    @PutMapping("/email-verification/{userId}/{otpCode}")
    public ResponseEntity<String> verifyUser(@PathVariable Long userId, @PathVariable String otpCode) {
        return authService.verifyUser(userId, otpCode);
    }

    // Update user details
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserModel> updateUser(@PathVariable Long userId, @RequestBody UserModel updatedUser) {
        UserModel user = authService.updateUser(userId, updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
