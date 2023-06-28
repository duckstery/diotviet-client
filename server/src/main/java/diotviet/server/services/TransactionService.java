package diotviet.server.services;

import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService extends BaseService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Transaction repository
     */
    @Autowired
    private TransactionRepository repository;

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
