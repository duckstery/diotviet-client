package diotviet.server.services;

import diotviet.server.constants.Status;
import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.utils.OtherUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {

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
     * Process order <br>
     * For both PENDING and PROCESSING, create a Transaction with provided amount <br>
     * If the amount is equal or surpass Order current payment amount, resolve Order
     *
     * @param order
     */
    public void process(Order order, Long amount) {
        // Get Order payment amount and convert to Long
        long paymentAmount = order.getPaymentAmount();
        // Parse provided amount
        long processAmount = amount;
        // Get paid amount
        long paidAmount = getPaidAmountOf(order);

        // Check if the amount is equal or surpass Order current payment amount
        if (processAmount >= (paymentAmount - paidAmount)) {
            // Resolve Order
            resolve(order, amount);
        } else {
            // Process Order and create a process transaction
            order.setStatus(Status.PROCESSING).getTransactions().add(createTransactionFor(order, amount));
        }
    }

    /**
     * Resolve order <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction
     *
     * @param order
     */
    public void resolve(Order order, Long amount) {
        // Create a resolve Transaction
        Transaction resolveTransaction;

        if (Objects.isNull(amount)) {
            // If amount is null, resolve by subtract paid amount from payment amount
            Long paymentAmount = order.getPaymentAmount();
            Long paidAmount = getPaidAmountOf(order);

            // Create Transaction
            resolveTransaction = createTransactionFor(order, paymentAmount - paidAmount);
        } else {
            // Else, apply requested amount as resolve amount
            resolveTransaction = createTransactionFor(order, amount);
        }

        // Setup Order
        order.setStatus(Status.RESOLVED)
                .setResolvedAt(new Date())
                .setTransactions(new ArrayList<>(List.of(resolveTransaction)));
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
                    // Peak through each transaction, soft delete and update reason for them
                    .peek(transaction -> transaction.setIsDeleted(true).setReason(reason))
                    .toList());
        } else {
            // Create a Transaction to set reason for Order
            transactions.add(createTransactionFor(order, 0L).setReason(reason));
        }

        // Setup Order
        order.setStatus(Status.ABORTED).setTransactions(transactions);
    }

    /**
     * Get Order paid amount
     *
     * @param order
     * @return
     */
    public Long getPaidAmountOf(Order order) {
        // Check if Order has any Transaction
        if (CollectionUtils.isEmpty(order.getTransactions())) {
            return 0L;
        }

        // If Order has some Transactions, iterate through those Transaction to see how much money is paid
        return order.getTransactions().stream()
                .map(Transaction::getAmount)
                .reduce(0L, Long::sum);
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
    private Transaction createTransactionFor(Order order, Long amount) {
        // Create Transaction and setup
        return new Transaction()
                .setAmount(amount)
                .setCreatedAt(OtherUtils.get(order.getResolvedAt(), new Date()))
                .setOrder(order);
    }
}
