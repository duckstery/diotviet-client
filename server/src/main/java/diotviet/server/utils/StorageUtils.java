package diotviet.server.utils;

import diotviet.server.exceptions.FileServingException;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
            output = Objects.isNull(filename) ? "" : StringUtils.strip(filename, "/").split("\\.")[1];
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

        return copy(
                multipartFile.getInputStream(),
                OtherUtils.hash(multipartFile.getBytes(), true),
                System.currentTimeMillis(),
                getExtension(multipartFile.getOriginalFilename())
        ).toString();
    }

    /**
     * Delete file
     *
     * @param filename
     * @throws IOException
     */
    public static void delete(String filename) throws IOException {
        // Resolve path
        Path absolutePath = resolve(filename);
        // Delete path
        Files.deleteIfExists(absolutePath);
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
                .filter(path -> path.getFileName().toString().equals(filename))
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
     * Pull image
     *
     * @param url
     * @return
     */
    public static String pull(String url, long timeSuffix) {
        if (Objects.isNull(url)) {
            return "";
        }

        // Open stream to URL
        try {
            // Try to init
            init();
            // Get filename in URL
            String filepath = URI.create(url).getPath();

            // Save file
            return copy(
                    new URL(url).openStream(),
                    FileNameUtils.getBaseName(filepath),
                    timeSuffix,
                    FileNameUtils.getExtension(filepath)
            ).toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Copy file to destination of ([hash]_[timeSuffix].[extension]
     *
     * @param is
     * @param hash
     * @param timeSuffix
     * @param extension
     * @return
     * @throws IOException
     */
    private static Path copy(InputStream is, String hash, long timeSuffix, String extension) throws IOException {
        // Generate filename
        String filename = hash + "_" + timeSuffix + "." + extension;
        // Create target path
        Path relativePath = FILES_ROOT.resolve(filename);

        // Save file to location
        Files.copy(is, PUBLIC_DIR.resolve(relativePath), StandardCopyOption.REPLACE_EXISTING);
        // Try to close input stream
        try {
            is.close();
        } catch (Exception ignored) {
        }

        return relativePath;
    }
}
