package diotviet.server.services.imports;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.Status;
import diotviet.server.constants.Type;
import diotviet.server.entities.*;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.repositories.OrderRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.services.GroupService;
import diotviet.server.utils.OtherUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderImportService extends BaseImportService<Order> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Order repository
     */
    @Autowired
    private OrderRepository orderRepository;
    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository customerRepository;
    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;

    // ****************************
    // Cache
    // ****************************

    /**
     * Cache Order
     */
    private Order order;
    /**
     * Cached Order's code
     */
    private String code = "";
    /**
     * Cache Customer
     */
    private Customer customer;
    /**
     * Cache Product
     */
    private Map<String, Product> productMap;
    /**
     * Cache Group
     */
    private HashMap<String, Group> groupMap;

    // ****************************
    // Public API
    // ****************************

    /**
     * Prepare to import Order
     *
     * @return
     */
    @Override
    public List<Order> prep() {
        // Init code
        initializeCode("DH", orderRepository::findFirstByCodeLikeOrderByCodeDesc);
        // Cache group map
        groupMap = new HashMap<>();
        for (Group group : groupService.getGroups(Type.TRANSACTION)) {
            groupMap.put(group.getName(), group);
        }
        // Cache product map
        productMap = new HashMap<>();
        for (Product product : productRepository.findAll()) {
            productMap.put(product.getTitle().trim(), product);
        }

        return null;
    }

    /**
     * Convert legacy to Order
     *
     * @param row
     * @return
     */
    @Override
    public Order convert(Row row) {
        // Check if you should fetch for new Order
        if (!this.code.equals(row.getCell(1).getRawValue())) {
            // Check if code is not empty
            if (StringUtils.isNotEmpty(this.code)) {
                // Then, save Order before fetching new Order
                orderRepository.save(this.order);
            }

            // Cache code
            this.code = row.getCell(1).getRawValue();
            // Try to fetch Order, if it's not exists, create a new Order. Skip if fail to fetch (or convert)
            if (!fetchOrConvertToOrder(row)) {
                this.code = "";
                return null;
            }
        }

        // Convert row to Item and add to Order
        this.order.getItems().add(convertToItem(row, order));

        return this.order;
    }

    /**
     * Re-attach any relationship
     *
     * @param orders
     * @return
     */
    @Override
    public void pull(List<Order> orders) {
        for (Order order : orders) {
            // Create new Set
            order.setGroups(order
                    .getGroups()
                    .stream()
                    .map(group -> groupMap.getOrDefault(group.getName(), null)) // Iterate through each staled Group, use it to pull from the persisted Group
                    .collect(Collectors.toCollection(HashSet::new)));
        }
    }

    /**
     * Import Order
     *
     * @param orders
     */
    @Override
    @Transactional
    public void runImport(List<Order> orders) {
        // Flush all cache
        this.flush();
    }

    /**
     * Flush cache
     */
    @Override
    public void flush() {
        order = null;
        customer = null;

        productMap.clear();
        productMap = null;

        groupMap.clear();
        groupMap = null;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Resolve phone number
     *
     * @param value
     * @return
     */
    private String resolvePhoneNumber(String value) {
        if (Objects.isNull(value)) {
            return null;
        }

        return value.length() > 15 ? value.substring(0, 15) : value;
    }

    /**
     * Convert row to new Order
     *
     * @param row
     * @return
     */
    private boolean fetchOrConvertToOrder(Row row) {
        // Try to fetch Order through database and cache
        this.order = orderRepository.findByCode(this.code).orElse(new Order());
        // Initiate Order's items list
        if (Objects.isNull(this.order.getItems())) {
            this.order.setItems(new ArrayList<>());
        }

        // Check if Order is fetched successfully
        if (this.order.getId() != 0) return true;

        try {
            // Try to fetch Customer. Return false if fail to fetch
            if (!fetchCustomer(row)) return false;

            // Set basic data
            this.order.setCustomer(this.customer);
            this.order.setCode(row.getCellText(1));
            this.order.setCreatedAt(DateUtils.parseDate(row.getCell(6).getRawValue(), "dd/MM/yyyy HH:mm:ss"));
            this.order.setResolvedAt(DateUtils.parseDate(row.getCell(6).getRawValue(), "dd/MM/yyyy HH:mm:ss"));
            this.order.setPhoneNumber(resolvePhoneNumber(row.getCell(9).getRawValue()));
            this.order.setAddress(row.getCell(10).getRawValue());
            this.order.setNote(row.getCell(28).getRawValue());
            this.order.setProvisionalAmount(row.getCell(29).getRawValue());
            this.order.setPaymentAmount(row.getCell(29).getRawValue());
            this.order.setDiscount(row.getCell(30).getRawValue());
            this.order.setDiscountUnit("cash");
            this.order.setStatus(Status.RESOLVED);
            this.order.setPoint(0L);
            this.order.setCreatedBy(OtherUtils.getRequester());
            this.order.setGroups(new HashSet<>(Collections.singletonList(groupMap.get(row.getCell(14).getRawValue()))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return
        return true;
    }

    /**
     * Convert row to new Item
     *
     * @param row
     * @return
     */
    private Item convertToItem(Row row, Order order) {
        // Create output
        Item item = new Item();

        try {
            // Set basic data
            item.setOrder(order);
            item.setProduct(productMap.get(row.getCell(40).getRawValue().trim()));
            item.setNote(row.getCell(42).getRawValue());
            item.setQuantity(Integer.parseInt(row.getCell(43).getRawValue()));
            item.setOriginalPrice(row.getCell(44).getRawValue());
            item.setDiscount(row.getCell(45).getRawValue());
            item.setDiscountUnit("cash");
            item.setActualPrice(row.getCell(46).getRawValue());
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Return
        return item;
    }

    /**
     * Fetch Customer
     *
     * @param row
     * @return
     */
    private boolean fetchCustomer(Row row) {
        // Customer
        QCustomer customer = QCustomer.customer;
        // Get data from Row
        String data = Arrays.stream(new Integer[]{8, 9, 10})
                .map(row::getCellText)
                .reduce("", StringUtils::join);

        // Check if cached Customer is usable
        if (Objects.nonNull(this.customer)) {
            // Join some string data of cached Customer
            String cachedCustomer = StringUtils.join(this.customer.getName(), this.customer.getPhoneNumber(), this.customer.getAddress());
            // Check if the Customer need to fetch is the cached one
            if (cachedCustomer.equals(data)) {
                return true;
            }
        }

        // Fetch Customer
        Optional<Customer> optional = customerRepository.findOne((new BooleanBuilder()).and(
                customer.name.coalesce("")
                        .concat(customer.phoneNumber.coalesce(""))
                        .concat(customer.address.coalesce(""))
                        .toLowerCase()
                        .contains(data.toLowerCase())));

        // Cache Customer
        optional.ifPresent(value -> this.customer = value);
        return optional.isPresent();
    }
}
