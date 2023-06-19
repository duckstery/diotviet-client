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
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * User model
 */
@Entity
@Table(name = "customers")
@Data
@Accessors(chain = true)
@QueryEntity
public class Customer {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq")
    @SequenceGenerator(name = "customers_seq", sequenceName = "customers_seq", allocationSize = 1)
    @CsvIgnore
    private long id;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName
    private String code;

    /**
     * Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @CsvCustomBindByName(converter = NameableField.class)
    @InitIgnore
    private Category category;

    /**
     * Group
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "assoc_customers_groups",
            joinColumns = {@JoinColumn(name = "customer_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    @InitIgnore
    @CsvCustomBindByName(converter = NameableSetField.class)
    private Set<Group> groups;

    /**
     * Name
     */
    @Column(length = 50)
    @CsvBindByName
    private String name;

    /**
     * Phone number
     */
    @Column(length = 13)
    @CsvBindByName
    private String phoneNumber;

    /**
     * House address
     */
    @Column(length = 100)
    @InitHide
    @CsvBindByName
    private String address;

    /**
     * Birthday
     */
    @Temporal(TemporalType.DATE)
    @InitHide
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthday;

    /**
     * Gender
     */
    @Column(nullable = false)
    @CsvBindByName
    private boolean isMale;

    /**
     * Email
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private String email;

    /**
     * Facebook profile URL
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private String facebook;

    /**
     * Description
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private String description;

    /**
     * Point
     */
    @Column
    @InitHide
    @CsvBindByName
    private Long point;

    /**
     * Image source
     */
    @Column
    @InitIgnore
    @CsvBindByName
    private String src;

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
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt = new Date();

    /**
     * Date of last order
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastOrderAt;

    /**
     * Date of last transaction
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastTransactionAt;

    /**
     * Is deleted flag
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    private Boolean isDeleted = Boolean.FALSE;
}
