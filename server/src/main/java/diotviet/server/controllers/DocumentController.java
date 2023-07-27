package diotviet.server.controllers;

import diotviet.server.entities.Order;
import diotviet.server.services.DocumentService;
import diotviet.server.templates.Document.DocumentInitResponse;
import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Document.DocumentDisplayView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        // First, get the Document content (or template)
        DocumentDisplayView document = service.init();
        // Second, get PrintableTag
        PrintableTag[] printableFields = entityUtils.getPrintableTag(document.getKey(), Order.class);

        return ok(new DocumentInitResponse(document, printableFields));
    }
}
