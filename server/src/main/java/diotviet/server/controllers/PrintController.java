package diotviet.server.controllers;

import diotviet.server.entities.Order;
import diotviet.server.templates.Print.PrintInitResponse;
import diotviet.server.templates.Print.PrintableTag;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/v1/print", produces = "application/json")
public class PrintController extends BaseController {
    // ****************************
    // Properties
    // ****************************



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
        // Get headers
        PrintableTag[] printableFields = entityUtils.getPrintableTag(Order.class);

        return ok(new PrintInitResponse(printableFields));
    }
}
