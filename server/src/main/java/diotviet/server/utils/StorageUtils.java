package diotviet.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import diotviet.server.structures.Tuple;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Other utility
 */
@Component
public class StorageUtils {

    // ****************************
    // Constants
    // ****************************

    /**
     * ImgBB account username
     */
    private static final String USERNAME = "ducdao";
    /**
     * ImgBB account password
     */
    private static final String PASSWORD = "z/PpH*J*7jigEiK";

    /**
     * ImgBB Login URL
     */
    private static final String IMG_BB_LOGIN_URL = "https://imgbb.com/login";
    /**
     * ImgBB Action URL
     */
    private static final String IMG_BB_ACTION_URL = "https://imgbb.com/json";
    /**
     * ImgBB Upload API
     */
    private static final String IMG_BB_UPLOAD_API = "https://api.imgbb.com/1/upload?key=5ae2315a8c7e1debf761ed1282f7c933";


    /**
     * Auth token form's key
     */
    private static final String TOKEN_FORM_KEY = "auth_token";
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
    /**
     * LID cookie's name
     */
    private static final String LID_COOKIE_KEY = "LID";
    /**
     * Auth token value find regex
     */
    private static final Pattern TOKEN_VALUE_REGEX = Pattern.compile("(?<=auth_token=\")[^\"]*");

    // ****************************
    // Properties
    // ****************************

    /**
     * PHP Session (Cookie)
     */
    private String phpSession = null;
    /**
     * Really don't know what is this
     */
    private String lid = null;
    /**
     * Authentication token
     */
    private String authToken = null;

    // ****************************
    // Public API
    // ****************************

    /**
     * Save file and generate filename
     *
     * @param file
     * @return
     */
    public JsonNode upload(MultipartFile file) throws IOException {
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
     * @param uIds
     */
    public void delete(List<String> uIds) {
        for (String uid : uIds) {
            try {
                // Try to log in
                tryToLoginToImgBB();

                // Create HttpEntity with form data
                HttpEntity<?> request = createRequestEntity(
                        Tuple.of(TOKEN_FORM_KEY, authToken),
                        Tuple.of("pathname", "/"),
                        Tuple.of("action", "delete"),
                        Tuple.of("single", "true"),
                        Tuple.of("delete", "image"),
                        Tuple.of("deleting[id]", uid)
                );

                // Request to delete and check if fail because of 403
                if (is403(RestUtils.requestForBody(IMG_BB_ACTION_URL, HttpMethod.POST, request))) {
                    // Force login
                    forceLoginToImgBB();
                    // Retry
                    RestUtils.request(IMG_BB_ACTION_URL, HttpMethod.POST, request);
                }
            } catch (Throwable ignored) {
                // Really don't care if deleted or not since ImgBB space is unlimited
                System.out.println("Failed to delete " + uid);
            }
        }
    }

    /**
     * Pull image
     *
     * @param url
     * @return
     */
    public String pull(String url, long timeSuffix) {
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
     * Create HttpEntity
     *
     * @param tuples
     * @return
     */
    @SafeVarargs
    private HttpEntity<?> createRequestEntity(Tuple<String, String>... tuples) {
        // Generate headers
        HttpHeaders headers = RestUtils.generateHeaders(false);
        // Cookie header
        List<String> cookieStrings = new ArrayList<>();

        // Set PHP Session
        if (StringUtils.isNotBlank(phpSession)) {
            cookieStrings.add(String.format("%s=%s", SESSION_COOKIE_KEY, phpSession));
        }
        // Set lid
        if (StringUtils.isNotBlank(lid)) {
            cookieStrings.add(String.format("%s=%s", LID_COOKIE_KEY, lid));
        }

        // Set Cookie header
        headers.set(HttpHeaders.COOKIE, String.join(";", cookieStrings));

        return new HttpEntity<>(RestUtils.craftFormData(tuples), headers);
    }

    /**
     * Try to log in to ImgBB server
     */
    public void tryToLoginToImgBB() {
        System.out.println(authToken);
        // Only login if no credential information is stored
        if (StringUtils.isBlank(authToken) || StringUtils.isBlank(phpSession)) {
            // Get PHP Session
            getPHPSessionAndAuthToken();

            // Create HttpEntity with username and password
            HttpEntity<?> request = createRequestEntity(
                    Tuple.of(TOKEN_FORM_KEY, authToken),
                    Tuple.of(USERNAME_FORM_KEY, USERNAME),
                    Tuple.of(PASSWORD_FORM_KEY, PASSWORD)
            );

            // Send login request to ImgBB and get response
            System.out.println(RestUtils.request(IMG_BB_LOGIN_URL, HttpMethod.POST, request));
        }
    }

    /**
     * Force login
     */
    public void forceLoginToImgBB() {
        // Clear all credential
        phpSession = null;
        lid = null;
        authToken = null;

        // Login
        tryToLoginToImgBB();
    }

    /**
     * Get PHP Session of ImgBB
     */
    private void getPHPSessionAndAuthToken() {
        // Create HttpEntity
        HttpEntity<?> request = createRequestEntity();
        // Send request to ImgBB to get response
        ResponseEntity<String> response = RestUtils.request(IMG_BB_LOGIN_URL, HttpMethod.GET, request);

        // Get Cookies in Set-Cookie header
        String cookiesString = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        if (Objects.isNull(cookiesString)) {
            throw new RuntimeException("PHP Session not found");
        }
        // Parse cookies
        List<HttpCookie> cookies = HttpCookie.parse(cookiesString);
        // Iterate through each cookie to find the PHP Session cookie
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals(SESSION_COOKIE_KEY)) {
                // Save cookie value
                phpSession = cookie.getValue();
                // Save auth token
                Matcher matcher = TOKEN_VALUE_REGEX.matcher(OtherUtils.get(response.getBody(), ""));
                if (matcher.find()) {
                    authToken = matcher.group();
                } else {
                    throw new RuntimeException("Auth token not found");
                }
                return;
            }
        }

        throw new RuntimeException("PHP Session not found");
    }

    /**
     * Check if having 403 (Auth session expired)
     *
     * @param body
     * @return
     */
    private boolean is403(JsonNode body) {
        System.out.println(body.get("error").get("code"));
        return body.get("error").get("code").asInt() == 403;
    }
}
