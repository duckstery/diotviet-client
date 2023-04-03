package diotviet.server.services;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Group repository
     */
    @Autowired
    private GroupRepository repository;


    // ****************************
    // Public API
    // ****************************

    /**
     * Get all groups and order by name (ASC) and id (DESC)
     *
     * @return
     */
    public List<Group> getGroups(Type type) {
        // Query for Product's data
        return repository.findAllByType(type, Sort.by("name").ascending().and(Sort.by("id").ascending()));
    }
}
