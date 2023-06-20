package diotviet.server.validators;

import diotviet.server.constants.Type;
import diotviet.server.entities.Customer;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.services.CategoryService;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@Component
public class CustomerValidator extends BaseValidator {

    // ****************************
    // Properties
    // ****************************

    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository customerRepository;
    /**
     * Group validator
     */
    @Autowired
    private GroupValidator groupValidator;
    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Customer validateAndExtract(CustomerInteractRequest request) {
        // Convert request to Customer
        Customer customer = map(request, Customer.class);

        // Check if request's group is not empty
        if (ArrayUtils.isNotEmpty(request.groups())) {
            // Check and get valid Group
            customer.setGroups(new HashSet<>(groupValidator.isExistByIds(request.groups())));
        }
        // Check and get the valid code
        checkCode(customer, "KH", customerRepository::findFirstByCodeAndIsDeletedFalse, customerRepository::findFirstByCodeLikeOrderByCodeDesc);
        // Check src
        checkImageSrc(customer);
        // Preserve Date type data
        checkDateData(customer);

        // Set category
        customer.setCategory(categoryService.getCategories(Type.PARTNER).stream().findFirst().orElseThrow());

        return customer;
    }

    /**
     * Generate code manually
     *
     * @return
     */
    public String generateCode() {
        return generateCode("KH", customerRepository::findFirstByCodeLikeOrderByCodeDesc);
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Check and preserve Date data
     *
     * @param customer
     */
    private void checkDateData(Customer customer) {
        // Check if Customer is not exist, so nothing need to be preserved
        Optional<Customer> readonly = customerRepository.findById(customer.getId());
        if (readonly.isEmpty()) {
            return;
        }

        // Get original customer to preserve Date data
        Customer original = readonly.get();

        // Set data for modified customer
        customer.setCreatedAt(original.getCreatedAt());
        customer.setLastOrderAt(original.getLastOrderAt());
        customer.setLastTransactionAt(original.getLastTransactionAt());
    }
}
