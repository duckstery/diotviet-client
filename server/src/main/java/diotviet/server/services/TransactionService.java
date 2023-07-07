package diotviet.server.services;

import diotviet.server.constants.Status;
import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.traits.BaseService;
import diotviet.server.utils.OtherUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * Resolve order <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction
     *
     * @param order
     */
    public void resolve(Order order) {
        // First, get Order amount out
        Long paymentAmount = Long.valueOf(order.getPaymentAmount());
        Long paidAmount = 0L;

        // Check if Order has any Transaction
        if (CollectionUtils.isNotEmpty(order.getTransactions())) {
            // If Order has some Transactions, iterate through those Transaction to see how much money is paid
            paidAmount = order.getTransactions().stream()
                    .map(Transaction::getAmount)
                    .map(Long::valueOf)
                    .reduce(0L, Long::sum);
        }

        // Setup Order
        order.setStatus(Status.RESOLVED);
        order.setResolvedAt(new Date());
        order.setTransactions(new ArrayList<>(List.of(createTransactionFor(order, String.valueOf(paymentAmount - paidAmount)))));
    }

    /**
     * Abort order
     * For PENDING, add a Transaction to save reason <br>
     * For PROCESSING and RESOLVED, their Transactions will be soft delete and have reason
     *
     * @param order
     * @return
     */
    public void abort(Order order, String reason) {
        // Create Transaction list for Order
        List<Transaction> transactions = new ArrayList<>();

        // Check if Order has any Transaction
        if (CollectionUtils.isNotEmpty(order.getTransactions())) {
            // Add all modified Transaction to new list
            transactions.addAll(order.getTransactions().stream()
                    .peek(transaction -> {
                        // Peak through each transaction, soft delete and update reason for them
                        transaction.setIsDeleted(true);
                        transaction.setReason(reason);
                    }).toList());
        } else {
            // Create a Transaction to set reason for Order
            Transaction transaction = createTransactionFor(order, "0");
            // Set reason
            transaction.setReason(reason);

            transactions.add(transaction);
        }

        // Setup Order
        order.setStatus(Status.ABORTED);
        order.setTransactions(transactions);
    }

    /**
     * Save all transaction
     *
     * @param transactions
     */
    public void saveAll(List<Transaction> transactions) {
        repository.saveAll(transactions);
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Create Transaction with amount for Order
     *
     * @param order
     * @param amount
     * @return
     */
    private Transaction createTransactionFor(Order order, String amount) {
        // Create Transaction
        Transaction transaction = new Transaction();

        // Setup Transaction
        transaction.setAmount(amount);
        transaction.setCreatedAt(OtherUtils.get(order.getResolvedAt(), new Date()));
        transaction.setOrder(order);

        return transaction;
    }
}
