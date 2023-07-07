package diotviet.server.templates.Order;

/**
 * Request to patch Product
 *
 * @param ids
 * @param versions
 * @param target
 * @param option
 */
public record OrderPatchRequest(
        Long[] ids,
        Long[] versions,
        String target,
        Long option,
        String reason
) {
}
