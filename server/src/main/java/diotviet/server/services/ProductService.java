package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.entities.QProduct;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductParam;
import diotviet.server.templates.Product.ProductSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.utils.StorageUtils;
import diotviet.server.views.Product.ProductDetailView;
import diotviet.server.views.Product.ProductSearchView;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    public void store(ProductParam param) throws IOException {
        // Common validate for create and update
        validate(param);

        // Try to add file first and save file src
        if (Objects.nonNull(param.getFile())) {
            param.setSrc(StorageUtils.save(param.getFile()));
        }

        // Create file
        productRepository.save(param);
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
            query.and(product.actualPrice.goe(request.minPrice()));
        }
        // Filter by max price
        if (Objects.nonNull(request.maxPrice()) && !request.minPrice().isBlank()) {
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
        if (Objects.nonNull(request.search()) && !request.search().isBlank()) {
            query.and(product.code.concat(product.title).contains(request.search()));
        }

        // Connect expression
        return query;
    }

    /**
     *
     */
    private void validate(ProductParam param) throws ValidationException {
        // Check if code is exist
        if (this.productRepository.existsByCode(param.getCode())) {
            throw new ServiceValidationException("exists_by", "product", "code");
        }
    }
}
