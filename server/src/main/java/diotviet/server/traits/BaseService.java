package diotviet.server.traits;

import diotviet.server.utils.StorageUtils;
import diotviet.server.views.Visualize;
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
     * @param defaultSrc
     * @return
     */
    protected <T> void saveFileFor(Visualize entity, MultipartFile file, BaseValidator<T> validator) {
        // Try to add file first and save file src
        if (Objects.nonNull(file)) {
            try {
                entity.setSrc(StorageUtils.save(file));
            } catch (IOException e) {
                validator.interrupt("upload_fail", "", "file");
            }
        }
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
