package diotviet.server.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.generators.NameableField;
import diotviet.server.generators.NameableSetField;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.Set;

/**
 * User model
 */
@Entity
@Table(name = "customer")
@Data
@Accessors(chain = true)
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
     * Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @CsvCustomBindByName(converter = NameableField.class)
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

    @Column(length = 13)
    @CsvBindByName
    private String phoneNumber;

    @Column(length = 100)
    @InitHide
    @CsvBindByName
    private String address;

    @Temporal(TemporalType.DATE)
    @InitHide
    @CsvBindByName
    private Date birthday;

    @Column(nullable = false)
    @CsvBindByName
    private boolean isMale;

    @Column
    @InitIgnore
    @CsvBindByName
    private String email;

    @Column
    @InitIgnore
    @CsvBindByName
    private String facebook;

    @Column
    @InitIgnore
    @CsvBindByName
    private String description;

    @Column
    @InitHide
    @CsvBindByName
    private Long point;

    @Column(length = 20)
    @InitIgnore
    @CsvBindByName
    private String createdBy;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @InitIgnore
    @CsvBindByName
    private Date createdAt;
}
