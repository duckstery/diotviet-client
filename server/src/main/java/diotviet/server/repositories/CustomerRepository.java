package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Customer;
import diotviet.server.traits.OptimisticLockRepository;
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
public interface CustomerRepository extends JpaRepository<Customer, Long>, QuerydslPredicateExecutor<Customer>, OptimisticLockRepository {
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
    <S extends Customer, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"category", "groups", "images"})
    <T> T findById(Long id, Class<T> classType);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"groups", "images"})
    <T> T findByIdAndIsDeletedFalse(Long id, Class<T> classType);

    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    List<Customer> findAll();

    /**
     * Get first by code
     *
     * @param code
     * @return
     */
    Customer findFirstByCodeAndIsDeletedFalse(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Customer findFirstByCodeLikeOrderByCodeDesc(String code);

    /**
     * Find with Group by id
     *
     * @return
     */
    @EntityGraph(attributePaths = {"groups"})
    Customer findWithGroupById(Long id);

    /**
     * Delete assoc between Group and Customer
     *
     * @param ids
     */
    @Modifying
    @Query(value = "DELETE FROM diotviet.assoc_groups_customers WHERE customer_id in :ids", nativeQuery = true)
    void deleteGroupAssocById(@Param("ids") Long[] ids);

    /**
     * Delete Customer by ID (this operation won't delete assoc, need to delete assoc first)
     *
     * @param ids
     */
    @Modifying
    @Query(value = "UPDATE Customer c SET c.isDeleted = true, c.version = c.version + 1 WHERE c.id in :ids AND c.isDeleted = false")
    void softDeleteByIds(@Param("ids") Long[] ids);
}
