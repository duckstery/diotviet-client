package diotviet.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import diotviet.server.entities.Image;
import diotviet.server.repositories.ImageRepository;
import diotviet.server.utils.StorageUtils;
import diotviet.server.validators.ImageValidator;
import diotviet.server.views.Identifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Image repository
     */
    @Autowired
    private ImageRepository repository;

    /**
     * Image validator
     */
    @Autowired
    private ImageValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Upload image
     *
     * @param files
     * @return
     */
    public void uploadAndSave(Identifiable entity, List<MultipartFile> files) {
        // Create output
        List<Image> images = new ArrayList<>();

        try {
            // Iterate through each MultipartFile
            for (MultipartFile file : files) {
                // Upload each MultipartFile to ImgBB
                JsonNode json = StorageUtils.upload(file);
                // Convert json to Image and add to images list
                images.add(findOrCreateImage(entity, json));
            }
        } catch (IOException e) {
            validator.interrupt("upload_fail", "", "file");
        }

        // First save all Images since no Cascade is set
        repository.saveAll(images);
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Convert JsonNode to Image
     *
     * @param json
     * @return
     */
    private Image findOrCreateImage(Identifiable entity, JsonNode json) {
        // Get root node
        JsonNode root = json.get("data");
        // Get uid
        String uid = root.get("id").asText("");

        // Try to find Image in db, if Image is not exists, create a new one
        Image image = repository.findByUid(uid).orElseGet(() -> (new Image())
                .setUid(uid)
                .setSrc(root.get("url").asText(""))
                .setIsDefault(false)
                .setIsActive(false)
                .setOwners(new ArrayList<>()));
        // Add new owner to Image
        image.getOwners().add(entity);

        return image;
    }
}
