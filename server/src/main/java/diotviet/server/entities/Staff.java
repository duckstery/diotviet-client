package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitField;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User model
 */
@Entity
@Table(name = "staffs")
@Data
@Accessors(chain = true)
@QueryEntity
public class Staff implements Identifiable, Lockable {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staffs_seq")
    @SequenceGenerator(name = "staffs_seq", sequenceName = "staffs_seq", allocationSize = 1)
    @CsvIgnore
    private long id;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName
    private String code;

    /**
     * User account
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @InitField("role")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    /**
     * Name
     */
    @Column(length = 50)
    @CsvBindByName
    private String name;

    /**
     * Phone number
     */
    @Column(length = 15)
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
    @CsvDate("yyyy-MM-dd")
    private LocalDate birthday;

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
    @InitHide
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assoc_image_identifiable",
            joinColumns = @JoinColumn(name = "identifiable_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    @WhereJoinTable(clause = "identifiable_type = 'staff'")
    @InitIgnore
    @CsvIgnore
    private List<Image> images;

    /**
     * Name of creator
     */
    @Column(length = 20)
    @InitHide
    @CsvBindByName
    private String createdBy;

    /**
     * LocalDateTime of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Is deleted flag
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvBindByName
    private Boolean isDeleted = Boolean.FALSE;

    /**
     * Is deactivated flag
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvBindByName
    private Boolean isDeactivated = Boolean.FALSE;

    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvIgnore
    private Long version = 0L;
}
