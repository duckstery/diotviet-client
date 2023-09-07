package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Product;
import diotviet.server.views.Product.ProductDisplayView;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {
    /**
     * Find by multiple condition
     *
     * @param predicate
     * @param queryFunction
     * @param <S>
     * @param <R>
     * @return
     */
    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    <S extends Product, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"category", "groups", "images"})
    <T> T findByIdAndIsDeletedFalse(Long id, Class<T> classType);

    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    List<Product> findAll();

    /**
     * Get Product that is in business to display
     *
     * @return
     */
    List<ProductDisplayView> findAllByIsInBusinessTrueAndIsDeletedFalse();

    /**
     * Get Product that is in business and is not deleted by ID
     *
     * @param ids
     * @return
     */
    List<Product> findByIdInAndIsInBusinessTrueAndIsDeletedFalse(List<Long> ids);

    /**
     * Count accumulatable Product
     *
     * @param ids
     * @return
     */
    long countByIdInAndCanBeAccumulatedTrueAndIsDeletedFalse(List<Long> ids);

    /**
     * Get first by code
     *
     * @param code
     * @return
     */
    Product findFirstByCodeAndIsDeletedFalse(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Product findFirstByCodeLikeOrderByCodeDesc(String code);

    /**
     * Find with Group by id
     *
     * @return
     */
    @EntityGraph(attributePaths = {"groups"})
    Product findWithGroupById(Long id);

    /**
     * Update Product isInBusiness flag
     *
     * @param isInBusiness
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p set p.isInBusiness = :isInBusiness WHERE p.id IN :ids AND p.isDeleted = false")
    void updateIsInBusinessByIds(@Param("isInBusiness") boolean isInBusiness, @Param("ids") Long[] ids);

    /**
     * Update Product canBeAccumulated flag
     *
     * @param canBeAccumulated
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p set p.canBeAccumulated = :canBeAccumulated WHERE p.id IN :ids AND p.isDeleted = false")
    void updateCanBeAccumulatedByIds(@Param("canBeAccumulated") boolean canBeAccumulated, @Param("ids") Long[] ids);

    /**
     * Delete assoc between Group and Product
     *
     * @param ids
     */
    @Modifying
    @Query(value = "DELETE FROM diotviet.assoc_groups_products WHERE product_id in :ids", nativeQuery = true)
    void deleteGroupAssocById(@Param("ids") Long[] ids);

    /**
     * Delete Products by ID (this operation won't delete assoc, need to delete assoc first)
     *
     * @param ids
     */
    @Modifying
    @Query(value = "UPDATE Product p SET p.isDeleted = true WHERE p.id in :ids AND p.isDeleted = false")
    void softDeleteByIds(@Param("ids") Long[] ids);
}
