package auth.auth_api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import auth.auth_api.model.UserModel;
import auth.auth_api.repository.AuthRepository;

@Service
public class AuthService {
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OTPService otpService;

    // Method to fetch a user by userId
    public UserModel getUserById(Long userId) {
        Optional<UserModel> user = authRepository.findById(userId);
        return user.orElse(null); // Return the user if found, otherwise return null
    }

    // Create a new user
    public UserModel createUser(UserModel user) {
        // Check if the user password is null
        if (user.getPwd() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        // Hash the password before saving it
        String hashedPassword = passwordEncoder.encode(user.getPwd());
        user.setPwd(hashedPassword);
        return authRepository.save(user);
    }

    // Verify a user's email
    public ResponseEntity<String> verifyUser(Long userId, String otpCode) {
        // Find the user by id
        Optional<UserModel> userOptional = authRepository.findById(userId);

        // Check if the user exists
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + userId);
        }

        // Extract the user from the Optional
        UserModel user = userOptional.get();

        // Check if the user is already verified
        if (user.isVerified()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already verified!");
        }

        // Verify the OTP code
        String storedOTP = otpService.getOTP(userId);
        if (storedOTP == null || !storedOTP.equals(otpCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP code!");
        }

        // Update the isVerified field to true
        user.setVerified(true);
        authRepository.save(user);

        // Remove the OTP code after successful verification
        otpService.removeOTP(userId);

        return ResponseEntity.ok("User verified successfully!");
    }

    // Update user details
    public UserModel updateUser(Long userId, UserModel updatedUser) {
        return authRepository.findById(userId)
                .map(user -> {
                    user.setFullName(updatedUser.getFullName());
                    user.setDob(updatedUser.getDob());
                    return authRepository.save(user);
                })
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
    }
}
