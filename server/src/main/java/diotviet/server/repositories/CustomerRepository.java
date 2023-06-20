package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.function.Function;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QuerydslPredicateExecutor<Customer> {
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
}
