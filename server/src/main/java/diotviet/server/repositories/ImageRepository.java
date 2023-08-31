package diotviet.server.repositories;

import diotviet.server.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, QuerydslPredicateExecutor<Image> {
    /**
     * Find by uid
     *
     * @param uid
     * @return
     */
    Optional<Image> findByUid(String uid);
}
