package diotviet.server.controllers;

import diotviet.server.entities.Customer;
import diotviet.server.entities.QProduct;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.templates.GeneralResponse;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.utils.StorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value="/api/fallback", produces="application/json")
public class FallbackController extends BaseController {

    @Autowired
    private EntityUtils entityUtils;

    @Autowired
    private StorageUtils storageUtils;

    @Autowired
    private TransactionRepository repository;

    @GetMapping("")
    public String index() {
//        StorageUtils.delete("3NKgDjW");
        return "Greetings from Spring Boot!";
    }

    @PostMapping ("/test")
    public String index(@RequestPart("file") MultipartFile file) throws IOException {
//        System.out.println(file.getName());
//        String fileId = storageUtils.upload(file).get("data").get("id").asText();
//        System.out.println(fileId);
        storageUtils.delete(List.of("6wwQKmc", "TTq6VcH"));
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/ping")
    public ResponseEntity<GeneralResponse> ping(@RequestParam("from") LocalDate from, @RequestParam("to") LocalDate to) {

        return ok(null);
    }
}
