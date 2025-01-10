package auth.auth_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Store the OTP code for a user with an expiration time (e.g. 5 minutes)
    public void storeOTP(Long userId, String otp) {
        String key = "otp:" + userId;
        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);
    }

    // Retrieve the OTP code for a user
    public String getOTP(Long userId) {
        String key = "otp:" + userId;
        return redisTemplate.opsForValue().get(key);

    }

    // Remove the OTP code after verification
    public void removeOTP(Long userId) {
        String key = "otp:" + userId;
        redisTemplate.delete(key);
    }
}
