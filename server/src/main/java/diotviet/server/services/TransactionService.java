package diotviet.server.services;

import diotviet.server.constants.Status;
import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.structures.DataPoint;
import diotviet.server.structures.Dataset;
import diotviet.server.templates.Transaction.TransactionSearchRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.views.Report.IncomeReportView;
import diotviet.server.views.Report.impl.IncomeReportByMonth;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                .setResolvedAt(LocalDateTime.now())
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
     * Report
     *
     * @param request
     * @return
     */
    public List<Dataset<String, Long>> report(TransactionSearchRequest request) {
        // Prepare expected_income dataset
        Dataset<String, Long> expectedIncome = Dataset.of("expected_income", "0", "blue");
        // Prepare real_income_inside dataset
        Dataset<String, Long> realIncomeInside = Dataset.of("real_income_inside", "1", "yellow");
        // Prepare real_income_outside dataset
        Dataset<String, Long> realIncomeOutside = Dataset.of("real_income_outside", "1", "purple");
        // Prepare usage dataset
        Dataset<String, Long> usage = Dataset.of("usage", "2", "red");

        // Get report by date
        List<IncomeReportView> report = repository.selectIncomeReportByCreatedAt(request.from(), request.to());

        // Check if display mode is by month
        if (StringUtils.equals(request.displayMode(), "month")) {
            report = groupReportByMonth(report);
        }

        // Iterate through each income report's entry
        for (IncomeReportView entry : report) {
            expectedIncome.add(DataPoint.of(entry.getTime(), entry.getExpectedIncome()));
            realIncomeInside.add(DataPoint.of(entry.getTime(), entry.getRealIncomeInside()));
            realIncomeOutside.add(DataPoint.of(entry.getTime(), entry.getRealIncomeOutside()));
            usage.add(DataPoint.of(entry.getTime(), entry.getUsage()));
        }

        return List.of(expectedIncome, realIncomeInside, realIncomeOutside, usage);
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
                .setCreatedAt(OtherUtils.get(order.getResolvedAt(), LocalDateTime.now()))
                .setOrder(order);
    }


    /**
     * Group IncomeReportView by month YearMonth of LocalDate
     *
     * @param report
     * @return
     */
    public List<IncomeReportView> groupReportByMonth(List<IncomeReportView> report) {
        // Create holder
        List<IncomeReportView> reportByMonth = new ArrayList<>();

        // Current month
        YearMonth current = null;
        // Current IncomeReportByMonth
        IncomeReportByMonth entryByMonth = null;

        // Iterate through each
        for (IncomeReportView entry : report) {
            // Get unit as LocalDate
            YearMonth time = YearMonth.parse(entry.getTime(), DateTimeFormatter.ISO_DATE);
            // Check if current month is null or not equals
            if (Objects.isNull(current) || !current.equals(time)) {
                // Assign current month
                current = time;
                // Create new entry and add to list
                reportByMonth.add(entryByMonth = new IncomeReportByMonth(current));
            }

            // Sum expectedIncome
            entryByMonth.addExpectedIncome(entry.getExpectedIncome());
            // Sum realIncomeInside
            entryByMonth.addRealIncomeInside(entry.getRealIncomeInside());
            // Sum realIncomeOutside
            entryByMonth.addRealIncomeOutside(entry.getRealIncomeOutside());
            // Sum usage
            entryByMonth.addUsage(entry.getUsage());
        }

        return reportByMonth;
    }
}
