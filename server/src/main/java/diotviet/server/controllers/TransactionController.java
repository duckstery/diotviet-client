package diotviet.server.controllers;

import diotviet.server.services.TransactionService;
import diotviet.server.structures.Dataset;
import diotviet.server.templates.Transaction.TransactionSearchRequest;
import diotviet.server.traits.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/transaction", produces = "application/json")
public class TransactionController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Transaction service
     */
    @Autowired
    private TransactionService service;

    // ****************************
    // Public API
    // ****************************

    /**
     * Store (Create) item
     *
     * @param name
     * @param code
     * @return
     */
    @GetMapping(value = "/report")
    public ResponseEntity<?> report(TransactionSearchRequest request) {
        // Report and get datasets
        List<Dataset<LocalDate, Long>> datasets = service.report(request);
        // Iterate through each dataset to set up metadata
        for (Dataset<LocalDate, Long> dataset : datasets) {
            dataset.applyLocalizationWithSupplier(this::__);
        }

        return ok(datasets);
    }
}
