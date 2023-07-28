package diotviet.server.validators;

import diotviet.server.entities.Document;
import diotviet.server.repositories.DocumentRepository;
import diotviet.server.traits.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Document validateAndExtract() {


        return null;
    }
}
