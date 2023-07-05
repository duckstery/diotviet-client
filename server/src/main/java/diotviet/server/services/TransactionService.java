package diotviet.server.services;

import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.traits.BaseService;
import diotviet.server.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public Transaction resolve(Order order) {
        // Create new Transaction instance
        Transaction transaction = new Transaction();

        // Setup data
        transaction.setAmount(order.getPaymentAmount());
        transaction.setCreatedAt(OtherUtils.get(order.getResolvedAt(), new Date()));
        transaction.setOrder(order);

        return transaction;
    }

    /**
     * Save all transaction
     *
     * @param transactions
     */
    public void saveAll(List<Transaction> transactions) {
        repository.saveAll(transactions);
    }
}
