package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;
import org.springframework.beans.factory.annotation.Value;

@PrintObject("item")
public interface OrderItemPrintView {
    @Value("#{target.product.title}")
    String getTitle();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(target.originalPrice)}")
    String getOriginalPrice();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(target.discount)}")
    String getDiscount();

    String getDiscountUnit();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(target.actualPrice)}")
    String getActualPrice();

    String getNote();

    int getQuantity();
}
