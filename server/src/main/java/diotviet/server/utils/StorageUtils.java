package diotviet.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import diotviet.server.structures.Tuple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;

/**
 * Other utility
 */
public class StorageUtils {

    // ****************************
    // Constants
    // ****************************

    /**
     * ImgBB account username
     */
    private static final String username = "ducdao";
    /**
     * ImgBB account password
     */
    private static final String password = "z/PpH*J*7jigEiK";

    /**
     * ImgBB Login URL
     */
    private static final String IMG_BB_LOGIN_URL = "https://imgbb.com/login";
    /**
     * ImgBB Upload API
     */
    private static final String IMG_BB_UPLOAD_API = "https://api.imgbb.com/1/upload?key=5ae2315a8c7e1debf761ed1282f7c933";


    /**
     * Auth token form's key
     */
    private static final String AUTH_FORM_KEY = "auth_token";
    /**
     * Username form's key
     */
    private static final String USERNAME_FORM_KEY = "login-subject";
    /**
     * Password form's key
     */
    private static final String PASSWORD_FORM_KEY = "password";
    /**
     * Session cookie's name
     */
    private static final String SESSION_COOKIE_KEY = "PHPSESSID";

    // ****************************
    // Properties
    // ****************************

    /**
     * PHP Session (Cookie
     */
    private static String phpSession;
    /**
     * Authentication token
     */
    private static String authToken;

    // ****************************
    // Public API
    // ****************************

    /**
     * Save file and generate filename
     *
     * @param file
     * @return
     */
    public static JsonNode upload(MultipartFile file) throws IOException {
        // Create HttpEntity
        HttpEntity<?> request = new HttpEntity<>(
                RestUtils.craftFormData(Tuple.of("image", Base64.getEncoder().encodeToString(file.getBytes()))),
                RestUtils.generateHeaders(true)
        );

        // Send file to ImgBB Upload API to save file
        return RestUtils.requestForBody(IMG_BB_UPLOAD_API, HttpMethod.POST, request);
    }

    /**
     * Delete file
     *
     * @param filename
     * @throws IOException
     */
    public static void delete(String filename) throws IOException {
        // Prevent deleting default image
        if (filename.contains("default.jpeg")) {
            return;
        }
        // Resolve path
        Path absolutePath = Path.of("");
        // Delete path
        Files.deleteIfExists(absolutePath);
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
            // Get filename in URL
            String filepath = URI.create(url).getPath();

            // Save file
            return "";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Login to ImgBB server
     */
    public static void loginToImgBB() {
        getPHPSession();
    }

    /**
     * Get PHP Session of ImgBB
     */
    private static void getPHPSession() {
        // Create HttpEntity
        HttpEntity<?> request = new HttpEntity<>(RestUtils.generateHeaders(false));
        // Send request to ImgBB to get response
        ResponseEntity<String> response = RestUtils.request(IMG_BB_LOGIN_URL, HttpMethod.GET, request);

        System.out.println(response.getHeaders());
    }
}
