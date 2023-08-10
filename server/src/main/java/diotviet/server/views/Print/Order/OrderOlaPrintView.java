package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;
import lombok.NoArgsConstructor;

@PrintObject("ola")
@NoArgsConstructor
public class OrderOlaPrintView {
    public String getText() {
        return "ahihi";
    }

    public Integer getNumber() {
        return 123;
    }
}
