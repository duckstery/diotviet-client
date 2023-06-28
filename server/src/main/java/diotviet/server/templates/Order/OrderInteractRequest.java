package diotviet.server.templates.Order;

import diotviet.server.templates.Order.Interact.OrderCustomer;
import diotviet.server.templates.Order.Interact.OrderItem;

import java.util.List;

public record OrderInteractRequest(
        Long id,
        String note,
        String provisionalAmount,
        String discount,
        String discountUnit,
        String paymentAmount,
        OrderCustomer customer,
        List<OrderItem> items
) {
}
