package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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
    @EntityGraph(attributePaths = {"category", "groups"})
    <T> T findById(Long id, Class<T> classType);

    /**
     * Get first by code
     *
     * @param code
     * @return
     */
    Product findFirstByCode(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Product findFirstByCodeLikeOrderByCodeDesc(String code);

    /**
     * Update Product isInBusiness flag
     *
     * @param isInBusiness
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p set p.isInBusiness = :isInBusiness WHERE p.id IN :ids")
    void updateIsInBusinessByIds(@Param("isInBusiness") boolean isInBusiness, @Param("ids") Long[] ids);

    /**
     * Update Product canBeAccumulated flag
     *
     * @param canBeAccumulated
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p set p.canBeAccumulated = :canBeAccumulated WHERE p.id IN :ids")
    void updateCanBeAccumulatedByIds(@Param("canBeAccumulated") boolean canBeAccumulated, @Param("ids") Long[] ids);

    /**
     * Delete Products by ID
     *
     * @param ids
     */
    @Modifying
    @Query("DELETE FROM Product p where p.id in :ids")
    void deleteByIds(@Param("ids") Long[] ids);
}
