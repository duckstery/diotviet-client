package diotviet.server.validators;

import diotviet.server.entities.Product;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductInteractRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;

@Component
public class ProductValidator extends BaseValidator {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * Category validator
     */
    @Autowired
    private CategoryValidator categoryValidator;
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
     */
    public Product validateAndExtract(ProductInteractRequest request) {
        // Convert request to Product
        Product product = map(request, Product.class);

        // Check and get the valid code
        product.setCode(this.isCodeValid(request.id(), request.code()));
        // Check and get valid Category
        product.setCategory(categoryValidator.isExistById(request.category()));
        // Check and get valid Group
        product.setGroups(new HashSet<>(groupValidator.isExistByIds(request.groups())));

        return product;
    }

    /**
     * Validate if code is valid, then return the valid code, else, interrupt
     *
     * @param code
     * @return
     */
    public String isCodeValid(Long id, String code) {
        if (Objects.isNull(code)) {
            return this.generateCode();
        }

        if (Objects.isNull(id)) {
            // Validate for "CREATE"
            if (code.startsWith("MS")) {
                // Check if code format is reserved
                throw new ServiceValidationException("reserved", "product", "code");
            } else if (Objects.nonNull(this.productRepository.findFirstByCodeAndIsDeletedFalse(code))) {
                // Check if code is exist
                throw new ServiceValidationException("exists_by", "product", "code");
            }
        } else {
            // Validate for "UPDATE"
            // Get first Product that has matched code
            Product product = this.productRepository.findFirstByCodeAndIsDeletedFalse(code);
            if (Objects.isNull(product) && code.startsWith("MS")) {
                // Check if Product with code is not exist and code format is reserved
                throw new ServiceValidationException("reserved", "product", "code");
            } else if (product.getId() != id) {
                // Else, check if Product exist and that Product is not self
                throw new ServiceValidationException("exists_by", "product", "code");
            }
        }

        return code;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Generate code
     *
     * @return
     */
    public String generateCode() {
        // Get Product with "largest" code
        Product product = productRepository.findFirstByCodeLikeOrderByCodeDesc("MS%");
        // Get number part from code
        String alphanumeric = Objects.isNull(product) ? "0" : product.getCode().substring(2);

        return String.format("MS%05d", Integer.parseInt(alphanumeric) + 1);
    }
}
