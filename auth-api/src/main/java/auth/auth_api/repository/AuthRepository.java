package auth.auth_api.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import auth.auth_api.model.UserModel;

public interface AuthRepository extends JpaRepository<UserModel, Long> {
    // Find a user by email
    UserModel findByEmail(String email);
}
