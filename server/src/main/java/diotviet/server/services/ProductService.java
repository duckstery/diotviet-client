package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.entities.QProduct;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.views.Product.ProductDetailView;
import diotviet.server.views.Product.ProductSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}
