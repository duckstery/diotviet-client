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
import diotviet.server.templates.Order.OrderSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.OrderValidator;
import diotviet.server.views.Order.OrderDetailView;
import diotviet.server.views.Order.OrderSearchView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService extends BaseService {

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
                (Integer) OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                (Integer) OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Order's data
        return repository.findBy(filter, q -> q.as(OrderSearchView.class).page(pageable));
    }

    /**
     * Get Product by id
     *
     * @param id
     * @return
     */
    public OrderDetailView findById(Long id) {
        return validator.isExist(repository.findById(id, OrderDetailView.class));
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void store(OrderInteractRequest request, Status status) {
        // Common validate for create and update
        Order order = validator.validateAndExtract(request);
        // Setup
        basicSetup(request, order);
        statusSetup(order, status);
        // For some reason, including this when saving Order cause multiple Selection point to Category and Group
        // For performance, Items will be saved separately by it repository
        List<Item> items = order.getItems();
        order.setItems(null);

        // Store and flush (immediate save to database), then proceed to store Product's Item
        repository.saveAndFlush(order);
        itemRepository.saveAll(items);
    }
//
//    /**
//     * Delete multiple item with ids
//     *
//     * @param ids
//     */
//    @Transactional(rollbackFor = {Exception.class, Throwable.class})
//    public void delete(Long[] ids) {
//        // Delete assoc
//        repository.deleteGroupAssocById(ids);
//        // Delete and get image path (this is physical resource, not database resource)
//        removeFiles(repository.softDeleteByIdsReturningSrc(ids));
//    }
//
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
        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(order.createdAt.goe(request.createAtFrom()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(order.createdAt.loe(request.createAtTo()));
        }
        // Filter by min resolvedAt
        if (Objects.nonNull(request.resolvedAtFrom())) {
            query.and(order.resolvedAt.goe(request.resolvedAtFrom()));
        }
        // Filter by max resolvedAt
        if (Objects.nonNull(request.resolvedAtTo())) {
            query.and(order.resolvedAt.loe(request.resolvedAtTo()));
        }
        // Filter by min price
        if (StringUtils.isNotBlank(request.priceFrom())) {
            query.and(order.paymentAmount.castToNum(Long.class).goe(Long.parseLong(request.priceFrom())));
        }
        // Filter by max price
        if (StringUtils.isNotBlank(request.priceTo())) {
            query.and(order.paymentAmount.castToNum(Long.class).loe(Long.parseLong(request.priceTo())));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            query.and(order
                    .code
                    .concat(order.phoneNumber)
                    .concat(order.address)
                    .concat(order.customer.name)
                    .toLowerCase()
                    .contains(request.search())
            );
        }

        // Connect expression
        return query;
    }

    /**
     * Basic info setup
     *
     * @param request
     * @param order
     */
    private void basicSetup(OrderInteractRequest request, Order order) {
        // Set created by
        order.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        // Count Product that can be accumulated and set point
        order.setPoint(productRepository.countByIdInAndCanBeAccumulatedTrueAndIsDeletedFalse(request
                .items()
                .stream()
                .map(OrderItem::id)
                .toList()));
        // Set address
        order.setAddress(order.getCustomer().getAddress());
        // Set phone number
        order.setPhoneNumber(order.getCustomer().getPhoneNumber());
        // Set customer last order at
        order.getCustomer().setLastOrderAt(order.getCreatedAt());
    }

    /**
     * Setup Order base on status
     *
     * @param order
     * @param status
     */
    private void statusSetup(Order order, Status status) {
        // Set status
        order.setStatus(status);
        // Check if order is resolved
        if (Status.RESOLVED.equals(status)) {
            // Set order resolved at
            order.setResolvedAt(new Date());
            // Use transaction service to resolve order (create Transaction instance)
            order.setTransactions(transactionService.resolve(order));
        }
    }
}
