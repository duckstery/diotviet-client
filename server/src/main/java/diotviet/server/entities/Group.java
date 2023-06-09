package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import diotviet.server.constants.Type;
import diotviet.server.views.Nameable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * Category but for user to create
 */
@Entity
@Table(name = "groups")
@Data
@Accessors(chain = true)
public class Group implements Nameable {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_seq")
    @SequenceGenerator(name = "groups_seq", sequenceName = "groups_seq", allocationSize = 1)
    private long id;

    /**
     * Name
     */
    @Column(length = 20)
    private String name;

    /**
     * Type
     */
    @Enumerated
    @Column(columnDefinition = "smallint")
    @JsonIgnore
    private Type type;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "groups")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Product> products;
}
