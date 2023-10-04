package diotviet.server.validators;

import diotviet.server.entities.Staff;
import diotviet.server.repositories.StaffRepository;
import diotviet.server.templates.Staff.StaffInteractRequest;
import diotviet.server.traits.BusinessValidator;
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
        Staff staff = map(request, Staff.class);
        // Check phone number
        checkPhoneNumber(staff);
        // Optimistic lock check
        checkOptimisticLock(staff, repository);
        // Check and get the valid code
        checkCode(staff, "NV", repository::findFirstByCodeAndIsDeletedFalse, repository::findFirstByCodeLikeOrderByCodeDesc);
        // Preserve LocalDate type data
        checkDateData(staff);

        return staff;
    }

    /**
     * Check if staff is valid by id
     *
     * @param id
     * @return
     */
    public Staff isValid(Long id) {
        // Get Staff that is not deleted by id
        Staff staff = repository.findByIdAndIsDeletedFalse(id, Staff.class);
        // Check if staff is not exist
        if (Objects.isNull(staff)) {
            interrupt("inconsistent_data", "staff");
        }

        return staff;
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
        assertStringRequired(request, "phoneNumber", 15);
        assertNumb(request, "role", true, 0, 4);
        assertStringNonRequired(request, "code", 0, 10);
    }

    /**
     * Check if phone number is used
     *
     * @param staff
     */
    private void checkPhoneNumber(Staff staff) {
        if (repository.existsByPhoneNumberAndIdNot(staff.getPhoneNumber(), staff.getId())) {
            interrupt("exists_by", "staff", "phoneNumber");
        }
    }

    /**
     * Check and preserve LocalDate data
     *
     * @param staff
     */
    private void checkDateData(Staff staff) {
        // Check if Staff is not exist, so nothing need to be preserved
        Optional<Staff> readonly = repository.findById(staff.getId());
        if (readonly.isEmpty()) {
            return;
        }

        // Get original staff to preserve LocalDate data
        Staff original = readonly.get();

        // Set data for modified staff
        staff.setCreatedAt(original.getCreatedAt());
    }
}
