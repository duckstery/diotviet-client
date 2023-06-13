package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
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
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private ProductRepository productRepository;
    /**
     * Product validator
     */
    @Autowired
    private ProductValidator validator;

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
    public Page<ProductSearchView> paginate(ProductSearchRequest request) {
        // Create filter
        BooleanBuilder filter = createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                (Integer) OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                (Integer) OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("code")
        );

        // Query for Product's data // .project("title") ??????????
        return productRepository.findBy(filter, q -> q.as(ProductSearchView.class).page(pageable));
    }

    /**
     * Get all displayable Product
     *
     * @return
     */
    public List<ProductDisplayView> display() {
        return productRepository.findAllByIsInBusinessTrue();
    }

    /**
     * Get Product by id
     *
     * @param id
     * @return
     */
    public ProductDetailView findById(Long id) {
        return productRepository.findById(id, ProductDetailView.class);
    }

    /**
     * Store item
     *
     * @param param
     */
    public void store(ProductInteractRequest request) {
        // Common validate for create and update
        Product product = validator.validateAndExtract(request);

        // Try to add file first and save file src
        if (Objects.nonNull(request.file())) {
            try {
                product.setSrc(StorageUtils.save(request.file()));
            } catch (IOException e) {
                validator.interrupt("upload_fail", "", "file");
            }
        }

        // Create file
        productRepository.save(product);
    }

    /**
     * Patch item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void patch(ProductPatchRequest request) {
        if (request.target().equals("business")) {
            productRepository.updateIsInBusinessByIds(request.option(), request.ids());
        } else if (request.target().equals("accumulating")) {
            productRepository.updateCanBeAccumulatedByIds(request.option(), request.ids());
        }
    }

    /**
     * Delete multiple item with ids
     *
     * @param ids
     */
    @Transactional
    public void delete(Long[] ids) {
        productRepository.deleteByIds(ids);
    }

    /**
     * Get all Product for export
     *
     * @return
     */
    public List<Product> export() {
        return productRepository.findAll();
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
        if (Objects.nonNull(request.minPrice()) && !request.minPrice().isBlank()) {
            query.and(product.actualPrice.castToNum(Long.class).goe(Long.parseLong(request.minPrice())));
        }
        // Filter by max price
        if (Objects.nonNull(request.maxPrice()) && !request.minPrice().isBlank()) {
            query.and(product.actualPrice.castToNum(Long.class).loe(Long.parseLong(request.maxPrice())));
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
        if (Objects.nonNull(request.search()) && !request.search().isBlank()) {
            query.and(product.code.concat(product.title).contains(request.search()));
        }

        // Connect expression
        return query;
    }
}
