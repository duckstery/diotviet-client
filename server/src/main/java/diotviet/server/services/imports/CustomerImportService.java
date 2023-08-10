package diotviet.server.services.imports;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Group;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.services.CategoryService;
import diotviet.server.services.GroupService;
import diotviet.server.utils.OtherUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerImportService extends BaseImportService<Customer> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository customerRepository;
    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;

    // ****************************
    // Cache
    // ****************************

    /**
     * Cache category
     */
    private Category category;
    /**
     * Cache Group
     */
    private HashMap<String, Group> groupMap;

    // ****************************
    // Public API
    // ****************************

    /**
     * Prepare to import Customer
     *
     * @return
     */
    @Override
    public List<Customer> prep() {
        // Init code
        initializeCode("KS", customerRepository::findFirstByCodeLikeOrderByCodeDesc);
        // Cache category
        category = categoryService.getCategories(Type.PARTNER).stream().findFirst().orElseThrow();
        // Cache group map
        groupMap = new HashMap<>();
        for (Group group : groupService.getGroups(Type.PARTNER)) {
            groupMap.put(group.getName(), group);
        }

        return new ArrayList<>();
    }

    /**
     * Convert legacy to Customer
     *
     * @param row
     * @return
     */
    @Override
    public Customer convert(Row row) {
        // Create output
        Customer customer = new Customer();

        try {
            // Set basic data
            customer.setCode(generateCode());
            customer.setCategory(category);
            customer.setName(row.getCell(3).getRawValue());
            customer.setPhoneNumber(resolvePhoneNumber(row.getCell(4).getRawValue()));
            customer.setAddress(row.getCell(5).getRawValue());
            customer.setBirthday(null);
            customer.setMale(resolveGender(row.getCell(11).getRawValue()));
            customer.setEmail(row.getCell(12).getRawValue());
            customer.setFacebook(row.getCell(13).getRawValue());
            customer.setDescription(row.getCell(15).getRawValue());
            customer.setPoint(resolvePoint(row.getCell(17).getRawValue()));
            customer.setCreatedAt(DateUtils.parseDate(row.getCell(19).getRawValue(), "dd/MM/yyyy"));
            customer.setCreatedBy(OtherUtils.getRequester());
            customer.setGroups(new HashSet<>(List.of(new Group[]{groupMap.get(row.getCell(14).getRawValue())})));
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Return
        return customer;
    }

    /**
     * Re-attach any relationship
     *
     * @param customers
     * @return
     */
    @Override
    public void pull(List<Customer> customers) {
        for (Customer customer : customers) {
            // Create new Set
            customer.setGroups(customer
                    .getGroups()
                    .stream()
                    .map(group -> groupMap.getOrDefault(group.getName(), null)) // Iterate through each staled Group, use it to pull from the persisted Group
                    .collect(Collectors.toCollection(HashSet::new)));
        }
    }

    /**
     * Import Customer
     *
     * @param customers
     */
    @Override
    @Transactional
    public void runImport(List<Customer> customers) {
        // Bulk insert
        customerRepository.saveAll(customers);
        // Flush all cache
        this.flush();
    }

    /**
     * Flush cache
     */
    @Override
    public void flush() {
        category = null;

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
     * Resolve gender
     *
     * @param value
     * @return
     */
    private Boolean resolveGender(String value) {
        return StringUtils.compare(value, "Nam") == 0;
    }

    /**
     * Resolve point
     *
     * @param value
     * @return
     */
    private Long resolvePoint(String value) {
        return Long.parseLong(OtherUtils.get(value, "0"));
    }
}
