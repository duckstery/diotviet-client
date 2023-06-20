package diotviet.server.services;

import diotviet.server.utils.StorageUtils;
import diotviet.server.validators.BaseValidator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class BaseService {
    // ****************************
    // Protected API
    // ****************************

    /**
     * Save file
     *
     * @param file
     * @param validator
     * @return
     */
    protected String saveFile(MultipartFile file, BaseValidator validator) {
        // Try to add file first and save file src
        if (Objects.nonNull(file)) {
            try {
                return StorageUtils.save(file);
            } catch (IOException e) {
                validator.interrupt("upload_fail", "", "file");
            }
        }

        return "";
    }

    /**
     * Remove files
     *
     * @param paths
     */
    protected void removeFiles(List<String> paths) {
        // Iterate through each path and delete it
        for (String path : paths) {
            try {
                StorageUtils.delete(Path.of(path).getFileName().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
