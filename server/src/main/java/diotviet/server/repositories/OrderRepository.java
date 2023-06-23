package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Order;
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
public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {
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
    @EntityGraph(attributePaths = {"groups", "customer"})
    <S extends Order, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

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
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"groups"})
    <T> T findByIdAndIsDeletedFalse(Long id, Class<T> classType);

    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    List<Order> findAll();

    /**
     * Get first by code
     *
     * @param code
     * @return
     */
    Order findFirstByCodeAndIsDeletedFalse(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Order findFirstByCodeLikeOrderByCodeDesc(String code);

    /**
     * Delete assoc between Group and Order
     *
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM diotviet.assoc_groups_orders WHERE order_id in :ids", nativeQuery = true)
    void deleteGroupAssocById(@Param("ids") Long[] ids);

    /**
     * Delete Order by ID (this operation won't delete assoc, need to delete assoc first)
     *
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE diotviet.orders SET is_deleted = true WHERE id in :ids AND is_deleted = false RETURNING src", nativeQuery = true)
    List<String> softDeleteByIdsReturningSrc(@Param("ids") Long[] ids);
}
