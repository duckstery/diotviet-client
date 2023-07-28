package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.entities.Order;
import diotviet.server.services.DocumentService;
import diotviet.server.services.GroupService;
import diotviet.server.templates.Document.DocumentInitResponse;
import diotviet.server.templates.Document.DocumentSelectResponse;
import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Document.DocumentInitView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/print", produces = "application/json")
public class DocumentController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Document service
     */
    @Autowired
    private DocumentService service;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;
    /**
     * Utilities for Entity interact
     */
    @Autowired
    private EntityUtils entityUtils;

    // ****************************
    // Public API
    // ****************************

    /**
     * Index page
     *
     * @return
     */
    @GetMapping("/index")
    public ResponseEntity<?> index() {
        // Get Group of Type.Print
        List<Group> groups = groupService.getGroups(Type.PRINT);
        // Init Document by group_id
        List<DocumentInitView> document = service.init(groups.get(0).getId());
        // Get PrintableTag
        PrintableTag[] printableFields = entityUtils.getPrintableTag(document.get(0).getKey(), Order.class);

        return ok(new DocumentInitResponse(groups, document, printableFields));
    }

    /**
     * Search by group id
     *
     * @return
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> searchByGroup(@PathVariable Long groupId) {
        return ok(new DocumentSelectResponse(service.init(groupId)));
    }

    /**
     * Find by id
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(service.findById(id));
    }
}
