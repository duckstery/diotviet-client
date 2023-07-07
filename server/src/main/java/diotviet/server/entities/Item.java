package diotviet.server.entities;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Product
 */
@Entity
@Table(name = "items")
@Data
@Accessors(chain = true)
@QueryEntity
public class Item {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @SequenceGenerator(name = "items_seq", sequenceName = "items_seq", allocationSize = 10)
//    @Column(columnDefinition = "bigint default nextval('diotviet.items_seq'::regclass)")
    private long id;

    /**
     * Customer
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    /**
     * Order
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

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

    /**
     * Note
     */
    @Column
    private String note;

    /**
     * Quantity
     */
    @Column
    private int quantity;
}
