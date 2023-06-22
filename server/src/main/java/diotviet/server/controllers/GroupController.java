package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.services.GroupService;
import diotviet.server.templates.Group.GroupInteractRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/v1/group", produces = "application/json")
public class GroupController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Index page
     *
     * @return
     */
    @GetMapping("/index/{code}")
    public ResponseEntity<?> index(@PathVariable int code) {
        return ok(groupService.getGroups(Type.fromCode(code)));
    }

    /**
     * Store (Create) item
     *
     * @param name
     * @param code
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> store(GroupInteractRequest groupInteractRequest) {
        // Store item
        groupService.store(groupInteractRequest);

        return ok("");
    }

    /**
     * Delete item
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestParam("ids") Long[] ids) {
        // Store item
        groupService.delete(ids);

        return ok("");
    }
}