package diotviet.server.validators;

import diotviet.server.entities.Category;
import diotviet.server.entities.Product;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.CategoryRepository;
import diotviet.server.repositories.GroupRepository;
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
        product.setCode(this.isCodeValid(request.code()));
        System.out.println(product.getCode());
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
    public String isCodeValid(String code) {
        if (Objects.isNull(code)) {
            return this.generateCode();
        }

        // Check if code format is reserved
        if (code.startsWith("MS")) {
            throw new ServiceValidationException("reserved", "product", "code");
        }
        // Check if code is exist
        if (this.productRepository.existsByCode(code)) {
            throw new ServiceValidationException("reserved", "product", "code");
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
    private String generateCode() {
        // Get Product with "largest" code
        Product product = productRepository.findFirstByCodeLikeOrderByCodeDesc("MS%");
        // Get number part from code
        String alphanumeric = Objects.isNull(product) ? "0" : product.getCode().substring(2);

        return String.format("MS%05d", Integer.parseInt(alphanumeric) + 1);
    }
}
