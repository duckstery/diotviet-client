package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.constants.Status;
import diotviet.server.entities.Item;
import diotviet.server.entities.Order;
import diotviet.server.entities.QOrder;
import diotviet.server.repositories.ItemRepository;
import diotviet.server.repositories.OrderRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Order.Interact.OrderItem;
import diotviet.server.templates.Order.OrderInteractRequest;
import diotviet.server.templates.Order.OrderPatchRequest;
import diotviet.server.templates.Order.OrderSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.OrderValidator;
import diotviet.server.views.Order.OrderDetailView;
import diotviet.server.views.Order.OrderSearchView;
import diotviet.server.views.Print.Order.OrderOrderPrintView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Order repository
     */
    @Autowired
    private OrderRepository repository;
    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * Item repository
     */
    @Autowired
    private ItemRepository itemRepository;
    /**
     * Transaction service
     */
    @Autowired
    private TransactionService transactionService;
    /**
     * Order validator
     */
    @Autowired
    private OrderValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Product
     *
     * @param request
     * @return
     */
    public Page<OrderSearchView> paginate(OrderSearchRequest request) {
        // Create filter
        BooleanBuilder filter = createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Order's data
        return repository.findBy(filter, q -> q.as(OrderSearchView.class).page(pageable));
    }

    /**
     * Get Order by id
     *
     * @param id
     * @return
     */
    public OrderDetailView findById(Long id) {
        return validator.isExist(repository.findById(id, OrderDetailView.class));
    }

    /**
     * Get Order print data
     *
     * @param id
     * @return
     */
    public OrderOrderPrintView print(Long id) {
        return validator.isExist(repository.findById(id, OrderOrderPrintView.class));
    }

    /**
     * Find code by id
     *
     * @param id
     * @return
     */
    public String findCodeById(Long id) {
        return repository.findCodeById(id);
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public Long store(OrderInteractRequest request, Status status) {
        // Common validate for create and update
        Order order = validator.validateAndExtract(request);
        // Setup
        basicSetup(request, order);
        // Check if order should be resolved
        if (Status.RESOLVED.equals(status)) {
            // Use transaction service to resolve Order
            transactionService.resolve(order, request.paymentAmount());
        }
        // For some reason, including this when saving Order cause multiple Selection point to Category and Group
        // For performance, Items will be saved separately by it repository
        List<Item> items = order.getItems();
        order.setItems(null);

        // Store and flush (immediate save to database), then proceed to store Product's Item
        Long id = repository.save(order).getId();
        itemRepository.saveAll(items);

        return id;
    }

    /**
     * Patch item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void patch(OrderPatchRequest request) {
        // Get status
        Status status = Status.fromCode(request.option());
        // Optimistic check
        validator.massCheckOptimisticLock(request.ids(), request.versions(), repository);
        // Get all Orders
        List<Order> orders = repository.findAllById(List.of(request.ids()));

        // Patch Orders
        switch (status) {
            case PROCESSING -> processOrders(orders, request.amount());
            case RESOLVED -> resolveOrders(orders, request.amount());
            case ABORTED -> abortOrders(orders, request.reason());
        }

        // Save
        repository.saveAll(orders);
    }

    /**
     * Search with a string
     *
     * @param request
     * @return
     */
    public List<OrderSearchView> query(OrderSearchRequest request) {
        return repository.findBy(createFilter(request), q -> q
                        .sortBy(Sort.by(Sort.Direction.DESC, "createdAt", "code"))
                        .limit(100)
                        .all())
                .stream()
                .map(this::calculateCurrentPaymentAmount)
                .toList();
    }
//
//    /**
//     * Get all Order for export
//     *
//     * @return
//     */
//    public List<Order> export() {
//        return repository.findAll();
//    }

    // ****************************
    // Private
    // ****************************

    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    private BooleanBuilder createFilter(OrderSearchRequest request) {
        // Get QOrder
        QOrder order = QOrder.order;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by groups
        if (Objects.nonNull(request.group())) {
            query.and(order.groups.any().id.eq(request.group()));
        }
        // Filter by type
        if (Objects.nonNull(request.status())) {
            query.and(order.status.in(Arrays.stream(request.status()).map(Status::fromCode).toArray(Status[]::new)));
        }
        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(order.createdAt.goe(request.createAtFrom().atStartOfDay()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(order.createdAt.loe(request.createAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by min resolvedAt
        if (Objects.nonNull(request.resolvedAtFrom())) {
            query.and(order.resolvedAt.goe(request.resolvedAtFrom().atStartOfDay()));
        }
        // Filter by max resolvedAt
        if (Objects.nonNull(request.resolvedAtTo())) {
            query.and(order.resolvedAt.loe(request.resolvedAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by min price
        if (Objects.nonNull(request.priceFrom())) {
            query.and(order.paymentAmount.goe(request.priceFrom()));
        }
        // Filter by max price
        if (Objects.nonNull(request.priceTo())) {
            query.and(order.paymentAmount.loe(request.priceTo()));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            query.and(order.code
                    .concat(order.phoneNumber.coalesce(""))
                    .concat(order.address.coalesce(""))
                    .concat(order.customer.name.coalesce(""))
                    .toLowerCase()
                    .contains(request.search().toLowerCase()));
        }

        // Connect expression
        return query;
    }

    /**
     * Calculate current payment amount for Order
     *
     * @param order
     * @return
     */
    private OrderSearchView calculateCurrentPaymentAmount(Order order) {
        // Create Projector
        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

        // Calculate Order current payment amount only for PROCESSING Order
        if (Status.PROCESSING.equals(order.getStatus())) {
            Long paymentAmount = order.getPaymentAmount();
            Long paidAmount = transactionService.getPaidAmountOf(order);
            order.setPaymentAmount(paymentAmount - paidAmount);
        }

        // Project OrderSearchView to Order
        return projectionFactory.createProjection(OrderSearchView.class, order);
    }

    /**
     * Basic info setup
     *
     * @param request
     * @param order
     */
    private void basicSetup(OrderInteractRequest request, Order order) {
        // Set created by
        order.setCreatedBy(UserService.getRequester())
                // Count Product that can be accumulated and set point
                .setPoint(productRepository.countByIdInAndCanBeAccumulatedTrueAndIsDeletedFalse(request
                        .items()
                        .stream()
                        .map(OrderItem::id)
                        .toList()))
                // Set address
                .setAddress(order.getCustomer().getAddress())
                // Set phone number
                .setPhoneNumber(order.getCustomer().getPhoneNumber())
                // Set customer last order at
                .getCustomer().setLastOrderAt(order.getCreatedAt());
    }

    /**
     * Resolve all Orders <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction <br>
     * For RESOLVED and ABORTED, throw Exception
     *
     * @param orders
     */
    private void processOrders(List<Order> orders, Long amount) {
        // Resolve Orders and save resolve Transaction
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING, PROCESSING -> transactionService.process(order, amount);
                // Do not allow to resolve RESOLVED and ABORTED
                case RESOLVED -> validator.abort("process_resolved_order");
                case ABORTED -> validator.abort("aborted_order");
            }
        }
    }

    /**
     * Resolve all Orders <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction <br>
     * For RESOLVED and ABORTED, throw Exception
     *
     * @param orders
     */
    private void resolveOrders(List<Order> orders, Long amount) {
        // Resolve Orders and save resolve Transaction
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING, PROCESSING -> transactionService.resolve(order, amount);
                // Do not allow to resolve RESOLVED and ABORTED
                case RESOLVED -> validator.abort("resolve_resolved_order");
                case ABORTED -> validator.abort("aborted_order");
            }
        }
    }

    /**
     * Abort all Orders
     * For PENDING, add a Transaction to save reason <br>
     * For PROCESSING and RESOLVED, their Transactions will be soft delete and have reason <br>
     * For ABORTED, throw Exception
     *
     * @param orders
     */
    private void abortOrders(List<Order> orders, String reason) {
        // Abort Orders
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING, PROCESSING, RESOLVED -> transactionService.abort(order, reason);
                // Do not allow to abort ABORTED
                case ABORTED -> validator.abort("aborted_order");
            }
        }
    }
}
