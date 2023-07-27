package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.constants.Status;
import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.QDocument;
import diotviet.server.repositories.DocumentRepository;
import diotviet.server.templates.Order.OrderSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.views.Document.DocumentDisplayView;
import diotviet.server.views.Order.OrderSearchView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
public class DocumentService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Document repository
     */
    @Autowired
    private DocumentRepository repository;
    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get all groups and document by name (ASC) and id (DESC)
     *
     * @return
     */
    public DocumentDisplayView init() {
        // Init Category and Group of Type.PRINT
        Category category = categoryService.init(Type.PRINT);
        Group group = groupService.init(Type.PRINT);

        // Query for Order's data
        return repository.findFirstByCategoryAndGroupOrderById(category, group);
    }
}
