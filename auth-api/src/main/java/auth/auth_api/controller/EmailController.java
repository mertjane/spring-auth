package auth.auth_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import auth.auth_api.model.UserModel;
import auth.auth_api.repository.AuthRepository;
import auth.auth_api.service.EmailService;
import auth.auth_api.service.OTPService;

@RestController
@RequestMapping("/api/v1/authorize")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private TemplateEngine templateEngine; // Inject Thymeleaf TemplateEngine

    @GetMapping("/email-verification")
    public String sendTestEmail(@RequestParam String email) {
        // Fetch the user by email
        UserModel user = authRepository.findByEmail(email);
        if (user == null) {
            return "User not found with email: " + email;
        }
        // Generate a 6-digit random code
        int genCode = (int) (Math.random() * 900000) + 100000;
        String otpCode = String.valueOf(genCode);

        // Store the OTP code in Redis
        otpService.storeOTP(user.getUserId(), otpCode);

        // Prepare the Thymeleaf context
        Context context = new Context();
        context.setVariable("code", otpCode); // Add the OTP code to the context

        // Render the HTML template
        String htmlBody = templateEngine.process("emails/email-verification", context);

        // Send the email
        String to = user.getEmail();
        String subject = "Your The Web code is " + otpCode;
        //String body = String.valueOf(otpCode);
        emailService.sendEmail(to, subject, htmlBody);
        return "Email sent successfully";
    }

}
