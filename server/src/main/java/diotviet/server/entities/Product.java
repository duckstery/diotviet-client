package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.generators.NameableField;
import diotviet.server.generators.NameableSetField;
import diotviet.server.views.Identifiable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.WhereJoinTable;

import java.util.List;
import java.util.Set;

/**
 * Product
 */
@Entity
@Table(name = "products")
@Data
@Accessors(chain = true)
@QueryEntity
public class Product implements Identifiable {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
    @SequenceGenerator(name = "products_seq", sequenceName = "products_seq", allocationSize = 1)
    @CsvIgnore
    private long id;

    /**
     * Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @CsvCustomBindByName(converter = NameableField.class)
    @ToString.Exclude
    private Category category;

    /**
     * Group
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "assoc_groups_products",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    @InitIgnore
    @CsvCustomBindByName(converter = NameableSetField.class)
    @ToString.Exclude
    private Set<Group> groups;

    /**
     * Access tokens
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "product")
    @JsonIgnore
    @InitIgnore
    @CsvIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Item> items;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName
    private String code;

    /**
     * Name
     */
    @Column(length = 50)
    @CsvBindByName
    private String title;

    /**
     * Description
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private String description;

    /**
     * Price before discount
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private Long originalPrice;

    /**
     * Discount's amount
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private Long discount;

    /**
     * Discount's unit
     */
    @Column(length = 4)
    @InitIgnore
    @CsvBindByName
    private String discountUnit;

    /**
     * Price after discount
     */
    @Column
    @CsvBindByName
    private Long actualPrice;

    /**
     * Measure's unit
     */
    @Column(length = 10)
    @CsvBindByName
    private String measureUnit;

    /**
     * Image source
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assoc_image_identifiable",
            joinColumns = @JoinColumn(name = "identifiable_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    @WhereJoinTable(clause = "identifiable_type = 'product'")
    @InitIgnore
    @CsvIgnore
    private List<Image> images;

    /**
     * Weight of product
     */
    @Column
    @InitHide
    @CsvBindByName
    private Integer weight;

    /**
     * Whether this production can be point accumulated
     */
    @Column(nullable = false)
    @CsvBindByName
    private Boolean canBeAccumulated = Boolean.TRUE;

    /**
     * Whether this production is currently saleable
     */
    @Column(nullable = false)
    @CsvBindByName
    private Boolean isInBusiness;

    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvBindByName
    private Boolean isDeleted = Boolean.FALSE;
}
