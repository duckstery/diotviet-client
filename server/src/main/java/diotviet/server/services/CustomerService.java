package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.DateExpression;
import diotviet.server.constants.PageConstants;
import diotviet.server.entities.QCustomer;
import diotviet.server.entities.QProduct;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.templates.Customer.CustomerSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.CustomerValidator;
import diotviet.server.views.Customer.CustomerSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CustomerService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private CustomerRepository customerRepository;
    /**
     * Product validator
     */
    @Autowired
    private CustomerValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Product
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public Page<CustomerSearchView> paginate(CustomerSearchRequest request) {
        // Create filter
        BooleanBuilder filter = createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                (Integer) OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                (Integer) OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Customer's data
        return customerRepository.findBy(filter, q -> q.as(CustomerSearchView.class).page(pageable));
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    private BooleanBuilder createFilter(CustomerSearchRequest request) {
        // Get QCustomer
        QCustomer customer = QCustomer.customer;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by groups
        if (Objects.nonNull(request.group())) {
            query.and(customer.groups.any().id.eq(request.group()));
        }
        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(customer.createdAt.goe(request.createAtFrom()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(customer.createdAt.loe(request.createAtTo()));
        }
        // Filter by min birthday
        if (Objects.nonNull(request.birthdayFrom())) {
            query.and(customer.birthday.goe(request.birthdayFrom()));
        }
        // Filter by max birthday
        if (Objects.nonNull(request.birthdayTo())) {
            query.and(customer.birthday.loe(request.birthdayTo()));
        }
        // Filter by min lastTransactionAt
        if (Objects.nonNull(request.lastTransactionAtFrom())) {
            query.and(customer.lastTransactionAt.goe(request.lastTransactionAtFrom()));
        }
        // Filter by max lastTransactionAt
        if (Objects.nonNull(request.lastTransactionAtTo())) {
            query.and(customer.lastTransactionAt.loe(request.lastTransactionAtTo()));
        }
        // Filter by isMale flag
        if (Objects.nonNull(request.isMale())) {
            query.and(customer.isMale.eq(request.isMale()));
        }
        // Filter by search string
        if (Objects.nonNull(request.search()) && !request.search().isBlank()) {
            query.and(customer.name.concat(customer.phoneNumber).concat(customer.address).contains(request.search()));
        }

        // Connect expression
        return query;
    }
}
