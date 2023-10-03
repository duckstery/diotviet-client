package diotviet.server.validators;

import diotviet.server.entities.Staff;
import diotviet.server.repositories.StaffRepository;
import diotviet.server.templates.Staff.StaffInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class StaffValidator extends BusinessValidator<Staff> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Staff repository
     */
    @Autowired
    private StaffRepository repository;
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
    public Staff validateAndExtract(StaffInteractRequest request) {
        // Primary validation
        validate(request);
        // Convert request to Staff
        Staff customer = Objects.isNull(request.id())
                ? map(request, Staff.class)
                : directMap(request, repository.findWithGroupById(request.id()));
        // Optimistic lock check
        checkOptimisticLock(customer, repository);
        // Check and get the valid code
        checkCode(customer, "KH", repository::findFirstByCodeAndIsDeletedFalse, repository::findFirstByCodeLikeOrderByCodeDesc);
        // Preserve LocalDate type data
        checkDateData(customer);

        return customer;
    }

    /**
     * Check if customer is valid by id
     *
     * @param id
     * @return
     */
    public Staff isValid(Long id) {
        // Get Staff that is not deleted by id
        Staff customer = repository.findByIdAndIsDeletedFalse(id, Staff.class);
        // Check if customer is not exist
        if (Objects.isNull(customer)) {
            interrupt("inconsistent_data", "customer");
        }

        return customer;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(StaffInteractRequest request) {
        assertStringRequired(request, "name", 50);
        assertStringNonRequired(request, "code", 0, 10);
        assertStringNonRequired(request, "phoneNumber", 0, 15);
        assertStringNonRequired(request, "address", 0, 11);
    }

    /**
     * Check and preserve LocalDate data
     *
     * @param customer
     */
    private void checkDateData(Staff customer) {
        // Check if Staff is not exist, so nothing need to be preserved
        Optional<Staff> readonly = repository.findById(customer.getId());
        if (readonly.isEmpty()) {
            return;
        }

        // Get original customer to preserve LocalDate data
        Staff original = readonly.get();

        // Set data for modified customer
        customer.setCreatedAt(original.getCreatedAt());
    }
}
