package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.data.StaffDAO;
import diotviet.server.repositories.StaffRepository;
import diotviet.server.templates.Staff.StaffSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.StaffValidator;
import diotviet.server.views.Staff.StaffDetailView;
import diotviet.server.views.Staff.StaffSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class StaffService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Staff repository
     */
    @Autowired
    private StaffRepository repository;
    /**
     * Staff DAO
     */
    @Autowired
    private StaffDAO dao;
    /**
     * Staff validator
     */
    @Autowired
    private StaffValidator validator;

    /**
     * Image service
     */
    @Autowired
    private ImageService imageService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Staff
     *
     * @param request
     * @return
     */
    public Page<StaffSearchView> paginate(StaffSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Staff's data
        return repository.findBy(filter, q -> q.as(StaffSearchView.class).page(pageable));
    }

    /**
     * Get Staff by id
     *
     * @param id
     * @return
     */
    public StaffDetailView findById(Long id) {
        return validator.isExist(repository.findByIdAndIsDeletedFalse(id, StaffDetailView.class));
    }

//    /**
//     * Store item
//     *
//     * @param request
//     */
//    @Transactional(rollbackFor = {Exception.class, Throwable.class})
//    public Staff store(StaffInteractRequest request) {
//        // Common validate for create and update
//        Staff customer = validator.validateAndExtract(request);
//        // Set createdBy
//        customer.setCreatedBy(UserService.getRequester());
//        // Pull Images and set Images, this step will make sure assoc between Staff and Images won't be deleted accidentally
//        customer.setImages(imageService.pull("customer", customer.getId()));
//        // Save customer
//        customer = repository.save(customer);
//
//        // Check if there is file to upload
//        if (Objects.nonNull(request.file())) {
//            // Save file and bind file to Staff
//            imageService.uploadAndSave(customer, List.of(request.file()));
//        }
//
//        return customer;
//    }
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
//        repository.softDeleteByIds(ids);
//        // Delete Image
//        imageService.delete("customer", ids);
//    }
//
//
//    /**
//     * Get all Staff for export
//     *
//     * @return
//     */
//    public List<Staff> export() {
//        return repository.findAll();
//    }
//
//    /**
//     * Search with a string
//     *
//     * @param request
//     * @return
//     */
//    public List<StaffSearchView> query(StaffSearchRequest request) {
//        return repository.findBy(dao.createFilter(request), q -> q.as(StaffSearchView.class).all());
//    }
//
//    /**
//     * Report
//     *
//     * @param request
//     * @return
//     */
//    public List<Dataset<String, Long>> report(RankReportRequest request) {
//        // Prepare expected_income dataset
//        Dataset<String, Long> totalIncome = Dataset.of("total_income", "0", "blue");
//        // Prepare real_income_inside dataset
//        Dataset<String, Long> orderedQuantity = Dataset.of("ordered_quantity", "1", "yellow");
//        // Prepare real_income_outside dataset
//        Dataset<String, Long> averageIncome = Dataset.of("average_income", "2", "red");
//
//        // Get report by date
//        List<Point<String, Long>> report = repository.selectTopReportByOrderCreatedAt(request.from(), request.to(), request.sort(), request.top());
//
//        // Iterate through each income report's entry
//        for (Point<String, Long> entry : report) {
//            // Since we union all top [top] in one List, we need to check each Dataset's size is equal [top] before proceed to next Dataset
//            if (totalIncome.size() < request.top()) {
//                // Check if totalIncome Dataset has enough DataPoint
//                totalIncome.add(entry);
//            } else if (orderedQuantity.size() < request.top()) {
//                // Check if totalIncome Dataset has enough DataPoint
//                orderedQuantity.add(entry);
//            } else {
//                // Add the rest to averageIncome
//                averageIncome.add(entry);
//            }
//        }
//
//        return List.of(totalIncome, orderedQuantity, averageIncome);
//    }
}
