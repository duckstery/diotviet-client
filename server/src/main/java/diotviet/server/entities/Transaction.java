package diotviet.server.entities;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "transactions")
@Data
@Accessors(chain = true)
@QueryEntity
public class Transaction {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
    @SequenceGenerator(name = "transactions_seq", sequenceName = "transactions_seq", allocationSize = 10)
    private long id;

    /**
     * Transactions
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Payed amount
     */
    @Column
    private Long amount;

    /**
     * Date of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt = new Date();

    /**
     * Reason of removal
     */
    @Column
    private String reason;

    /**
     * Is deleted flag
     */
    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Boolean isDeleted = Boolean.FALSE;
}
