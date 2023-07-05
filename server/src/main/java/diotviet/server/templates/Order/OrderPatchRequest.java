package diotviet.server.templates.Order;

/**
 * Request to patch Product
 *
 * @param ids
 * @param target
 * @param code
 */
public record OrderPatchRequest(
        Long[] ids,
        String target,
        Long code
) {
}
