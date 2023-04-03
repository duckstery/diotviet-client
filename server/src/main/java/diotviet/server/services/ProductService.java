package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.entities.Product;
import diotviet.server.entities.QProduct;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductSearchRequest;
import diotviet.server.utils.OtherUtils;
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
    public Page<Product> paginate(ProductSearchRequest request) {
        // Create filter
        BooleanBuilder filter = createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                (Integer) OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                (Integer) OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("code")
        );

        // Query for Product's data
        return productRepository.findAll(filter, pageable);
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
        if (Objects.nonNull(request.category())) {
            query.and(product.category.id.eq(request.category()));
        }
        // Filter by groups
        if (Objects.nonNull(request.groups())) {
            query.and(product.groups.any().id.in(request.groups()));
        }
        // Filter by min price
        if (Objects.nonNull(request.minPrice())) {
            query.and(product.actualPrice.gt(request.minPrice()));
        }
        // Filter by max price
        if (Objects.nonNull(request.maxPrice())) {
            query.and(product.actualPrice.lt(request.maxPrice()));
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
        if (Objects.nonNull(request.search())) {
            query.and(product.code.concat(product.title).eq(request.search()));
        }

        // Connect expression
        return query;
    }
}
