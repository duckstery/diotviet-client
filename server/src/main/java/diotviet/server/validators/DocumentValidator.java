package diotviet.server.validators;

import diotviet.server.entities.Document;
import diotviet.server.repositories.DocumentRepository;
import diotviet.server.templates.Document.DocumentInteractRequest;
import diotviet.server.traits.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DocumentValidator extends BaseValidator<Document> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Document repository
     */
    @Autowired
    private DocumentRepository repository;
    /**
     * Group validator
     */
    @Autowired
    private GroupValidator groupValidator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Document validateAndExtract(DocumentInteractRequest request) {
        // Primary validation
        validate(request);
        // Convert request to Customer
        Document document = map(request, Document.class);

        // Check if request's groupId is not empty
        if (Objects.nonNull(request.groupId())) {
            // Check and get valid Group
            document.setGroup(groupValidator.isExistById(request.groupId()));
        }
        // Check if Document's id should be 0 (for adding)
        if (Objects.isNull(request.id()) || request.id() < 0) {
            // Set id to 0
            document.setId(0);
        }
        // Optimistic lock check
        checkOptimisticLock(document, repository);

        return document;
    }

    /**
     * Check if Group has at least 1 Document except for target
     *
     * @param groupId
     * @param id
     */
    public void mustHasAtLeastOneExceptFor(Long groupId, Long id) {
        // Check count
        if (repository.countByGroupIdAndIdNot(groupId, id) == 0) {
            interrupt("least_document", "document");
        }
    }


    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(DocumentInteractRequest request) {
        assertStringRequired(request, "name", 20);
    }
}
