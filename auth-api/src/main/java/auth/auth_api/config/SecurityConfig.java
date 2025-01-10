package auth.auth_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth.html", "/signupPwd.html", "/emailVerify.html", "/about-you.html",
                                "/css/**", "/js/**", "/assets/**")
                        .permitAll()
                        .requestMatchers("/api/v1/authorize/**").permitAll()
                        .requestMatchers("/api/v1/authorize/signup").permitAll()
                        .requestMatchers("/api/v1/authorize/email-verification/**").permitAll()
                        .requestMatchers("/api/v1/authorize/update-user/**").permitAll()

                        .anyRequest().authenticated());
        return http.build();
    }
}