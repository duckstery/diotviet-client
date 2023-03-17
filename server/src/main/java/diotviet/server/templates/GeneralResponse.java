package diotviet.server.templates;

/**
 * General response object
 *
 * @param success
 * @param message
 * @param data
 */
public record GeneralResponse(boolean success, String message, Object data) {
    /**
     * Success response body with message
     *
     * @param message
     * @param data
     * @return
     */
    public static GeneralResponse success(String message, Object data) {
        return new GeneralResponse(true, message, data);
    }

    /**
     * Success response body without message
     *
     * @param data
     * @return
     */
    public static GeneralResponse success(Object data) {
        return new GeneralResponse(true, "", data);
    }

    /**
     * Fail response body with message
     *
     * @param message
     * @param data
     * @return
     */
    public static GeneralResponse fail(String message, Object data) {
        return new GeneralResponse(true, message, data);
    }

    /**
     * Fail response body without message
     *
     * @param data
     * @return
     */
    public static GeneralResponse fail(Object data) {
        return new GeneralResponse(true, "", data);
    }
}
