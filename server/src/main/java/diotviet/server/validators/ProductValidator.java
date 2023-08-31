package diotviet.server.validators;

import diotviet.server.entities.Product;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;

@Component
public class ProductValidator extends BusinessValidator<Product> {

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
        // Primary validate
        validate(request);
        // Convert request to Product
        Product product = map(request, Product.class);

        // Check if request's category is not null
        if (Objects.nonNull(request.category())) {
            // Check and get valid Category
            product.setCategory(categoryValidator.isExistById(request.category()));
        }
        // Check if request's group is not empty
        if (ArrayUtils.isNotEmpty(request.groups())) {
            // Check and get valid Group
            product.setGroups(new HashSet<>(groupValidator.isExistByIds(request.groups())));
        }
        // Check code
        checkCode(product, "MS", productRepository::findFirstByCodeAndIsDeletedFalse, productRepository::findFirstByCodeLikeOrderByCodeDesc);

        return product;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(ProductInteractRequest request) {
        assertStringRequired(request, "title", 50);
        assertStringNonRequired(request, "code", 0, 10);
        assertNumb(request, "originalPrice", true, 0, 999999999999L);
        assertNumb(request, "actualPrice", true, 0, 999999999999L);
        assertNumb(request, "discount", true, 0, 999999999999L);
        assertStringNonRequired(request, "measureUnit", 0, 10);
        assertNumb(request, "weight", false, 0, 1000);
    }
}
