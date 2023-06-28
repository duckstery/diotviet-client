package diotviet.server.templates.Order.Interact;

public record OrderItem(
        Long id,
        String note,
        String originalPrice,
        String discount,
        String discountUnit,
        String actualPrice,
        Integer quantity
) {
}
