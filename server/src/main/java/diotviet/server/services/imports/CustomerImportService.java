package diotviet.server.services.imports;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Customer;
import diotviet.server.repositories.CategoryRepository;
import diotviet.server.repositories.GroupRepository;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.services.CategoryService;
import diotviet.server.services.GroupService;
import diotviet.server.utils.OtherUtils;
import diotviet.server.utils.StorageUtils;
import diotviet.server.validators.CustomerValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerImportService implements BaseImportService<Customer> {

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
    /**
     * Customer validator
     */
    @Autowired
    private CustomerValidator validator;

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
    /**
     * Time of import
     */
    private Long timer;

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
        // Cache category
        category = categoryService.getCategories(Type.PARTNER).stream().findFirst().orElseThrow();
        // Cache group map
        groupMap = new HashMap<>();
        for (Group group : groupService.getGroups(Type.PARTNER)) {
            groupMap.put(group.getName(), group);
        }

        // Mark time of Import
        timer = System.currentTimeMillis();

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
            customer.setCode(validator.generateCode());
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
     * @param e
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
        return Long.parseLong((String) OtherUtils.get(value, "0"));
    }
}
