package diotviet.server.templates.Order;

import diotviet.server.views.Customer.CustomerSearchView;
import org.springframework.data.domain.Page;

public record OrderSearchResponse(Page<CustomerSearchView> items) {
}
