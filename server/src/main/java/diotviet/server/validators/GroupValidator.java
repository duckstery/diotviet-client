package diotviet.server.validators;

import diotviet.server.entities.Group;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GroupValidator extends BaseValidator {

    // ****************************
    // Properties
    // ****************************

    /**
     * Group repository
     */
    @Autowired
    private GroupRepository groupRepository;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate for Group existence and return it
     *
     * @param ids
     * @return
     */
    public List<Group> isExistByIds(Long[] ids) {
        if (Objects.isNull(ids)) {
            return null;
        }

        // Check if groups are exists
        List<Group> group = groupRepository.findAllById(List.of(ids));
        if (group.size() != ids.length) {
            throw new ServiceValidationException("invalid_groups", "", "groups");
        }

        return group;
    }
}
