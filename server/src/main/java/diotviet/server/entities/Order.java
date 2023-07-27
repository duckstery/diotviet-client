package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.annotations.PrintTag;
import diotviet.server.constants.Status;
import diotviet.server.generators.NameableField;
import diotviet.server.generators.NameableSetField;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User model
 */
@NamedEntityGraph(
        name = "order_detail",
        attributeNodes = {
                @NamedAttributeNode("groups"),
                @NamedAttributeNode("customer"),
                @NamedAttributeNode(value = "items", subgraph = "item_product")
        },
        subgraphs = {@NamedSubgraph(name = "item_product", attributeNodes = {@NamedAttributeNode("product")})}
)
@Entity
@Table(name = "orders")
@Data
@Accessors(chain = true)
@QueryEntity
public class Order implements Identifiable, Lockable {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", allocationSize = 10)
    @CsvIgnore
    private long id;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName
    @PrintTag
    private String code;

    /**
     * Group
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "assoc_groups_orders",
            joinColumns = {@JoinColumn(name = "order_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    @CsvCustomBindByName(converter = NameableSetField.class)
    @InitIgnore
    @ToString.Exclude
    private Set<Group> groups;

    /**
     * Customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @CsvCustomBindByName(converter = NameableField.class)
    @ToString.Exclude
    @PrintTag(component = Customer.class)
    private Customer customer;

    /**
     * Items
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "order")
    @CsvCustomBindByName(converter = NameableSetField.class)
    @InitIgnore
    @ToString.Exclude
    @PrintTag(component = Item.class, isIterable = true)
    private List<Item> items;

    /**
     * Transactions
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "order")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvIgnore
    @InitIgnore
    private List<Transaction> transactions;

    /**
     * Phone number
     */
    @Column(length = 15)
    @CsvBindByName
    @PrintTag
    private String phoneNumber;

    /**
     * House address
     */
    @Column(length = 100)
    @InitHide
    @CsvBindByName
    @PrintTag
    private String address;

    /**
     * Price before discount
     */
    @Column(length = 11)
    @InitIgnore
    @CsvBindByName
    @PrintTag
    private String provisionalAmount;

    /**
     * Discount's amount
     */
    @Column(length = 11)
    @InitIgnore
    @CsvBindByName
    @PrintTag
    private String discount;

    /**
     * Discount's unit
     */
    @Column(length = 4)
    @InitIgnore
    @CsvBindByName
    @PrintTag
    private String discountUnit;

    /**
     * Price after discount
     */
    @Column(length = 11)
    @CsvBindByName
    @PrintTag
    private String paymentAmount;

    /**
     * Status
     */
    @Enumerated
    @Column(columnDefinition = "smallint")
    private Status status = Status.PENDING;

    /**
     * Point
     */
    @Column
    @InitHide
    @CsvBindByName
    private Long point;

    /**
     * Description
     */
    @Column
    @InitIgnore
    @CsvBindByName
    @PrintTag
    private String note;

    /**
     * Name of creator
     */
    @Column(length = 20)
    @InitHide
    @CsvBindByName
    private String createdBy;

    /**
     * Date of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @PrintTag
    private Date createdAt = new Date();

    /**
     * Date of resolve
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date resolvedAt;

    /**
     * Version
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvIgnore
    private Long version = 0L;
}
