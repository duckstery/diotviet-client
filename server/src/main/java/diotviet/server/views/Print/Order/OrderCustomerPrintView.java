package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;

import java.util.Date;

@PrintObject("customer")
public interface OrderCustomerPrintView {
    String getName();

    Date getBirthday();

    String getEmail();
}
