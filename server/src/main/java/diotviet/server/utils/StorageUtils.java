package diotviet.server.utils;

import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

/**
 * Other utility
 */
public class StorageUtils {

    // ****************************
    // Constants
    // ****************************

    // Public directory
    public static final Path PUBLIC_DIR = Path.of(System.getProperty("user.home"), ".diotviet");
    public static final Path FILES_ROOT = Path.of("files");

    // ****************************
    // Properties
    // ****************************

    // Flag to mark that PUBLIC_DIR is created
    private static boolean isInit = false;

    // ****************************
    // Public API
    // ****************************

    /**
     * Try to init storage
     */
    public static void init() throws IOException {
        System.out.println(PUBLIC_DIR.resolve(FILES_ROOT));

        // Check if PUBLIC_DIR is not exist
        if (!isInit && Files.notExists(PUBLIC_DIR, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectory(PUBLIC_DIR);
            Files.createDirectory(PUBLIC_DIR.resolve(FILES_ROOT));
        }
    }

    /**
     * Save file and generate public URL
     *
     * @param multipartFile
     * @return
     */
    public static String save(MultipartFile multipartFile) throws IOException {
        // Try to init
        init();

        // Get file extension
        String extension = multipartFile.getOriginalFilename().split("\\.")[1];
        // Generate filename
        String filename = OtherUtils.hash(multipartFile.getBytes()) + "_" + System.currentTimeMillis() + "." + extension;
        // Create target path
        Path relativePath = FILES_ROOT.resolve(filename);

        // Save file to location
        Files.copy(multipartFile.getInputStream(),PUBLIC_DIR.resolve(relativePath), StandardCopyOption.REPLACE_EXISTING);

        return relativePath.toString();
    }

    /**
     * Sort
     *
     * @param list
     * @return
     */
    public static List<String> sort(List<String> list) {
        // Sort list
        Collections.sort(list);

        return list;
    }
}
