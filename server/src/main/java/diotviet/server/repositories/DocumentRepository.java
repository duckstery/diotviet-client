package diotviet.server.repositories;

import diotviet.server.entities.Category;
import diotviet.server.entities.Document;
import diotviet.server.entities.Group;
import diotviet.server.traits.OptimisticLockRepository;
import diotviet.server.views.Document.DocumentDisplayView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, OptimisticLockRepository {
    /**
     * Find by multiple condition
     *
     * @param category
     * @param group
     * @return
     */
    DocumentDisplayView findFirstByCategoryAndGroupOrderById(Category category, Group group);
}
