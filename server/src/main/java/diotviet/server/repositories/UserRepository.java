package diotviet.server.repositories;

import diotviet.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find User by email
     *
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if an User is exists by email
     *
     * @param email
     * @return
     */
    Boolean existsByEmail(String email);
}
