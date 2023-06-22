package diotviet.server.validators;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.GroupRepository;
import diotviet.server.templates.Group.GroupInteractRequest;
import diotviet.server.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GroupValidator extends BaseValidator<Group> {

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

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Group validateAndExtract(GroupInteractRequest request) {
        // Primary validation
        assertStringRequired(request, "name" , 20);
        assertObject(request, "type", true);

        // Convert request to Group
        Group group = new Group();
        if (Objects.nonNull(request.id())) {
            group.setId(request.id());
        }
        group.setName(request.name());
        group.setType(Type.fromCode(request.type()));

        return group;
    }
}
