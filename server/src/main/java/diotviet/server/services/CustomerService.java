package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.data.CustomerDAO;
import diotviet.server.entities.Customer;
import diotviet.server.entities.QCustomer;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import diotviet.server.templates.Customer.CustomerSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.CustomerValidator;
import diotviet.server.views.Customer.CustomerDetailView;
import diotviet.server.views.Customer.CustomerSearchView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class CustomerService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository repository;
    /**
     * Customer DAO
     */
    @Autowired
    private CustomerDAO dao;
    /**
     * Product validator
     */
    @Autowired
    private CustomerValidator validator;

    /**
     * Image service
     */
    @Autowired
    private ImageService imageService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Product
     *
     * @param request
     * @return
     */
    public Page<CustomerSearchView> paginate(CustomerSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Customer's data
        return repository.findBy(filter, q -> q.as(CustomerSearchView.class).page(pageable));
    }

    /**
     * Get Product by id
     *
     * @param id
     * @return
     */
    public CustomerDetailView findById(Long id) {
        return validator.isExist(repository.findByIdAndIsDeletedFalse(id, CustomerDetailView.class));
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public Customer store(CustomerInteractRequest request) {
        // Common validate for create and update
        Customer customer = validator.validateAndExtract(request);
        // Set createdBy
        customer.setCreatedBy(UserService.getRequester());
        // Pull Images and set Images, this step will make sure assoc between Customer and Images won't be deleted accidentally
        customer.setImages(imageService.pull("customer", customer.getId()));
        // Save customer
        customer = repository.save(customer);

        // Check if there is file to upload
        if (Objects.nonNull(request.file())) {
            // Save file and bind file to Customer
            imageService.uploadAndSave(customer, List.of(request.file()));
        }

        return customer;
    }

    /**
     * Delete multiple item with ids
     *
     * @param ids
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void delete(Long[] ids) {
        // Delete assoc
        repository.deleteGroupAssocById(ids);
        // Delete and get image path (this is physical resource, not database resource)
        repository.softDeleteByIds(ids);
        // Delete Image
        imageService.delete("customer", ids);
    }


    /**
     * Get all Customer for export
     *
     * @return
     */
    public List<Customer> export() {
        return repository.findAll();
    }

    /**
     * Search with a string
     *
     * @param request
     * @return
     */
    public List<CustomerSearchView> query(CustomerSearchRequest request) {
        return repository.findBy(dao.createFilter(request), q -> q.as(CustomerSearchView.class).all());
    }
}
