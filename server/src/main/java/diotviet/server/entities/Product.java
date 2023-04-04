package diotviet.server.entities;

import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

/**
 * Product
 */
@Entity
@Table(name = "products")
@Data
@Accessors(chain = true)
@QueryEntity
public class Product {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    @SequenceGenerator(name = "products_seq", sequenceName = "products_seq", allocationSize = 1)
    @InitHide
    private long id;

    /**
     * Category
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    /**
     * Group
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "assoc_product_groups",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    @InitIgnore
    private Set<Group> groups;

    /**
     * Code
     */
    @Column(length = 3)
    private String code;

    /**
     * Name
     */
    @Column(length = 50)
    private String title;

    /**
     * Description
     */
    @Column
    @InitIgnore
    private String description;

    /**
     * Price before discount
     */
    @Column(length = 11)
    @InitIgnore
    private String originalPrice;

    /**
     * Discount's amount
     */
    @Column(length = 11)
    @InitIgnore
    private String discount;

    /**
     * Discount's unit
     */
    @Column(length = 4)
    @InitIgnore
    private String discountUnit;

    /**
     * Price after discount
     */
    @Column(length = 11)
    private String actualPrice;

    /**
     * Measure's unit
     */
    @Column(length = 10)
    private String measureUnit;

    /**
     * Image source
     */
    @Column
    @InitIgnore
    private String src;

    /**
     * Weight of product
     */
    @Column(length = 8)
    @InitHide
    private String weight;

    /**
     * Whether this production can be point accumulated
     */
    @Column(nullable = false)
    private Boolean canBeAccumulated = Boolean.TRUE;

    /**
     * Whether this production is currently saleable
     */
    @Column(nullable = false)
    private Boolean isInBusiness;
}
