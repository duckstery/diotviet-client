package diotviet.server.services;

import diotviet.server.repositories.DocumentRepository;
import diotviet.server.validators.DocumentValidator;
import diotviet.server.views.Document.DocumentInitView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Document repository
     */
    @Autowired
    private DocumentRepository repository;
    /**
     * Document validator
     */
    @Autowired
    private DocumentValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Init Document by group id
     *
     * @param groupId
     * @return
     */
    public List<DocumentInitView> init(Long groupId) {
        // Find all documents by groupId
        List<DocumentInitView> documents = repository.findAllByGroupIdOrderById(groupId);
        // Then, re-fetch the first Document (in Documents) with data
        documents.set(0, repository.findById(documents.get(0).getId(), DocumentInitView.class));

        return documents;
    }

    /**
     * Find by id
     *
     * @param id
     * @return
     */
    public DocumentInitView findById(Long id) {
        return validator.isExist(repository.findById(id, DocumentInitView.class));
    }
}
