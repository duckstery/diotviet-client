package diotviet.server.controllers;

import diotviet.server.exceptions.FileServingException;
import diotviet.server.utils.StorageUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Path;

@Controller
@RequestMapping(produces = "application/json")
public class FileController {

    // ****************************
    // Public API
    // ****************************

    /**
     * Serve image
     *
     * @param name
     * @return
     * @throws IOException
     */
    @GetMapping("/files/{name}")
    public ResponseEntity<?> serveImage(@PathVariable String name) throws IOException {
        // Resolve filename
        Path target = StorageUtils.resolve(name);
        // Get extension
        String extension = StorageUtils.getExtension(target.toString());

        return ResponseEntity
                .ok()
                .contentType(extension.equalsIgnoreCase("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(StorageUtils.serve(target)));
    }
}
