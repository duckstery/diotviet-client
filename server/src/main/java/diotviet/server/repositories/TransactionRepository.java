package diotviet.server.repositories;

import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {
    /**
     * Filter id of Orders that have Transaction
     *
     * @param orders
     * @return
     */
    @Query("SELECT t.order.id FROM Transaction t WHERE t.order IN :orders")
    List<Long> filterOrderIdsWithTransaction(@Param("orders") List<Order> orders);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Transaction t SET t.isDeleted = true, t.reason = :reason WHERE t.order.id IN :ids")
    void softDeleteTransactionByOrderIds(@Param("ids") List<Long> ids, @Param("reason") String reason);
}
