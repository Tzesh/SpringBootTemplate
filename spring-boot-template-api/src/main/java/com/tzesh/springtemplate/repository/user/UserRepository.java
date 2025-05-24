package com.tzesh.springtemplate.repository.user;

import com.tzesh.springtemplate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for users
 * @author tzesh
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by email
     * @param username User email
     * @return Optional of User
     */
    Optional<User> findByUsername(String username);

    /** Check if user exists by email and username
     * @param username User email
     * @param email User email
     * @return boolean
     */
    boolean existsByUsernameOrEmail(String username, String email);
}
