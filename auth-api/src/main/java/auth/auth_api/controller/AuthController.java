package auth.auth_api.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import auth.auth_api.dto.AuthResponse;
import auth.auth_api.dto.LoginRequest;
import auth.auth_api.model.UserModel;
import auth.auth_api.service.AuthService;
import auth.auth_api.service.JwtService;

@RestController
@RequestMapping("/api/v1/authorize")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);

            // Store JWT in Redis (you'll need to inject RedisTemplate)
            redisTemplate.opsForValue().set(userDetails.getUsername(), jwt, jwtService.getExpirationTime(),
                    TimeUnit.MILLISECONDS);

            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (BadCredentialsException e) {
            // Handle invalid email or password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (AuthenticationException e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

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
