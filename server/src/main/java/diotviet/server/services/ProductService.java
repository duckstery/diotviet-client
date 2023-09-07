package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.entities.Item;
import diotviet.server.entities.Order;
import diotviet.server.entities.Product;
import diotviet.server.entities.QProduct;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductInteractRequest;
import diotviet.server.templates.Product.ProductPatchRequest;
import diotviet.server.templates.Product.ProductSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.utils.StorageUtils;
import diotviet.server.validators.ProductValidator;
import diotviet.server.views.Product.ProductDetailView;
import diotviet.server.views.Product.ProductDisplayView;
import diotviet.server.views.Product.ProductSearchView;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private ProductRepository repository;
    /**
     * Product validator
     */
    @Autowired
    private ProductValidator validator;

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
    public Page<ProductSearchView> paginate(ProductSearchRequest request) {
        // Create filter
        BooleanBuilder filter = createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("code")
        );

        // Query for Product's data // .project("title") ??????????
        return repository.findBy(filter, q -> q.as(ProductSearchView.class).page(pageable));
    }

    /**
     * Get all displayable Product
     *
     * @return
     */
    public List<ProductDisplayView> display() {
        return repository.findAllByIsInBusinessTrueAndIsDeletedFalse();
    }

    /**
     * Get Product by id
     *
     * @param id
     * @return
     */
    public ProductDetailView findById(Long id) {
        return validator.isExist(repository.findByIdAndIsDeletedFalse(id, ProductDetailView.class));
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void store(ProductInteractRequest request) {
        // Common validate for create and update, then save it
        Product product = repository.save(validator.validateAndExtract(request));

        // Check if there is file to upload
        if (Objects.nonNull(request.file())) {
            // Save file and bind file to Customer
            imageService.uploadAndSave(product, List.of(request.file()));
        }
    }

    /**
     * Patch item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void patch(ProductPatchRequest request) {
        if (request.target().equals("business")) {
            repository.updateIsInBusinessByIds(request.option(), request.ids());
        } else if (request.target().equals("accumulating")) {
            repository.updateCanBeAccumulatedByIds(request.option(), request.ids());
        }
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
        imageService.delete("product", ids);
    }

    /**
     * Get all Product for export
     *
     * @return
     */
    public List<Product> export() {
        return repository.findAll();
    }

    /**
     * Produce a Product Item
     *
     * @param items
     * @return
     */
    public List<Item> produce(List<Item> items, Order order) {
        // Get Product base on requested Item
        List<Product> products = repository.findByIdInAndIsInBusinessTrueAndIsDeletedFalse(items.stream().map(Item::getId).toList());

        // Iterate through each Item in items
        for (Item item : items) {
            // Find matching Product to produce
            Product match = IterableUtils.find(products, product -> product.getId() == item.getId());
            // If no matching, it means Item is invalid
            if (Objects.isNull(match)) return null;
            // Set original Product for Item
            item.setProduct(match)
                    // Clear item's id since it was Product's id
                    .setId(0)
                    // Set order
                    .setOrder(order);
        }

        return items;
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
    private BooleanBuilder createFilter(ProductSearchRequest request) {
        // Get QProduct
        QProduct product = QProduct.product;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by category
        if (Objects.nonNull(request.categories())) {
            query.and(product.category.id.in(request.categories()));
        }
        // Filter by groups
        if (Objects.nonNull(request.group())) {
            query.and(product.groups.any().id.eq(request.group()));
        }
        // Filter by min price
        if (Objects.nonNull(request.minPrice())) {
            query.and(product.actualPrice.goe(request.minPrice()));
        }
        // Filter by max price
        if (Objects.nonNull(request.maxPrice())) {
            query.and(product.actualPrice.loe(request.maxPrice()));
        }
        // Filter by canBeAccumulated flag
        if (Objects.nonNull(request.canBeAccumulated())) {
            query.and(product.canBeAccumulated.eq(request.canBeAccumulated()));
        }
        // Filter by isInBusiness flag
        if (Objects.nonNull(request.isInBusiness())) {
            query.and(product.isInBusiness.eq(request.isInBusiness()));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            query.and(product.code.concat(product.title.coalesce("")).toLowerCase().contains(request.search().toLowerCase()));
        }

        // Connect expression
        return query.and(product.isDeleted.isFalse());
    }
}
