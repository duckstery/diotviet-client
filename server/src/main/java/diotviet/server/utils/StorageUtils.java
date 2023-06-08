package diotviet.server.utils;

import diotviet.server.exceptions.FileServingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        // Check if PUBLIC_DIR is not exist
        if (!isInit && Files.notExists(PUBLIC_DIR, LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectory(PUBLIC_DIR);
            Files.createDirectory(PUBLIC_DIR.resolve(FILES_ROOT));
        }
    }

    /**
     * Get file extension
     *
     * @param filename
     * @return
     */
    public static String getExtension(String filename) {
        // Output
        String output = "";
        try {
            output = Objects.isNull(filename) ? "" : filename.split("\\.")[1];
        } catch (Exception ignored) {
        }

        return output;
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
        String extension = getExtension(multipartFile.getOriginalFilename());
        // Generate filename
        String filename = OtherUtils.hash(multipartFile.getBytes()) + "_" + System.currentTimeMillis() + "." + extension;
        // Create target path
        Path relativePath = FILES_ROOT.resolve(filename);

        // Save file to location
        Files.copy(multipartFile.getInputStream(), PUBLIC_DIR.resolve(relativePath), StandardCopyOption.REPLACE_EXISTING);

        return relativePath.toString();
    }

    /**
     * Check for file existence and resolve filename
     *
     * @param filename
     * @throws IOException
     */
    public static Path resolve(String filename) throws IOException {
        // Try to init
        init();

        // Get matched file
        Path target = Files
                .list(PUBLIC_DIR.resolve(FILES_ROOT))
                .filter(path -> path.toString().contains(filename))
                .findFirst()
                .orElse(null);
        // Check if file is exists
        if (Objects.isNull(target)) {
            throw new FileServingException("file_not_exist");
        }

        return target;
    }

    /**
     * Serve file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static InputStream serve(Path file) throws IOException {
        // Try to init
        init();

        return Files.newInputStream(file);
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
