package diotviet.server.controllers;

import diotviet.server.entities.Product;
import diotviet.server.exceptions.FileUploadingException;
import diotviet.server.services.imports.ProductImportService;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Support imports from legacy (KiotViet)
 */
@Controller
@RequestMapping(value = "/api/v1/legacy", produces = "application/json")
public class LegacyImportController extends BaseController {

    // ****************************
    // Properties
    // ****************************

    /**
     * Service
     */
    @Autowired
    private ProductImportService service;

    // ****************************
    // Public API
    // ****************************

    /**
     * Import KiotViet Product
     *
     * @param file
     * @return
     */
    @PostMapping(value = "product/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> importCSV(@RequestPart("file") MultipartFile file) {
        // Create Product list
        List<Product> products = service.prep();
        // Open stream to file
        openStream(file, row -> products.add(service.convert(row)));
        // Import data
        service.runImport(products);

        return ok("");
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Open stream to read XLSX
     *
     * @param file
     * @param action
     */
    public void openStream(MultipartFile file, Consumer<Row> action) {
        // Read xlsx file
        try (ReadableWorkbook workbook = new ReadableWorkbook(file.getInputStream())) {
            // Get first sheet
            Sheet sheet = workbook.getFirstSheet();
            // Read through each row
            sheet.openStream().skip(1).forEach(action);
        } catch (IOException ignored) {
            throw new FileUploadingException();
        }
    }
}
