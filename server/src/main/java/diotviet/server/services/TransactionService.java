package diotviet.server.services;

import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService extends BaseService {

    // ****************************
    // Public API
    // ****************************

    /**
     * Resolve order
     *
     * @param order
     */
    public List<Transaction> resolve(Order order) {
        // Create new Transaction instance
        Transaction transaction = new Transaction();

        // Setup data
        transaction.setAmount(order.getPaymentAmount());
        transaction.setCreatedAt(order.getResolvedAt());
        transaction.setOrder(order);

        return List.of(transaction);
    }
}
