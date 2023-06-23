package diotviet.server.entities;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Product
 */
@Entity
@Table(name = "versions")
@Data
@Accessors(chain = true)
@QueryEntity
public class Version {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "versions_seq")
    @SequenceGenerator(name = "versions_seq", sequenceName = "versions_seq", allocationSize = 1)
    private long id;

    /**
     * Customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Price before discount
     */
    @Column(length = 11)
    private String originalPrice;

    /**
     * Discount's amount
     */
    @Column(length = 11)
    private String discount;

    /**
     * Discount's unit
     */
    @Column(length = 4)
    private String discountUnit;

    /**
     * Price after discount
     */
    @Column(length = 11)
    private String actualPrice;
}
