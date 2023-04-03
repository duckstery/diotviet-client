package diotviet.server.repositories;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    /**
     * Find all groups of a Type
     *
     * @param type
     * @param sort
     * @return
     */
    List<Group> findAllByType(Type type, Sort sort);
}
