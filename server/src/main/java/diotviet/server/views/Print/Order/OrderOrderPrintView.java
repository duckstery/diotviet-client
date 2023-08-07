package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;
import diotviet.server.annotations.PrintTag;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

@PrintObject("order")
public interface OrderOrderPrintView {
    @PrintTag(ignored = true)
    String getId();

    @PrintTag(isIdentifier = true)
    String getCode();

    @PrintTag(component = OrderCustomerPrintView.class)
    OrderCustomerPrintView getCustomer();

    @PrintTag(component = OrderItemPrintView.class, isIterable = true)
    List<OrderItemPrintView> getItems();

    String getPhoneNumber();

    String getAddress();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(target.provisionalAmount)}")
    String getProvisionalAmount();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(target.discount)}")
    String getDiscount();

    String getDiscountUnit();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(target.paymentAmount)}")
    String getPaymentAmount();

    String getNote();

    Date getCreatedAt();
}
