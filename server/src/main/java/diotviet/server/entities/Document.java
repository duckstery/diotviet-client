package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.generators.NameableField;
import diotviet.server.generators.NameableSetField;
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
@Table(name = "documents")
@Data
@Accessors(chain = true)
public class Document {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_seq")
    @SequenceGenerator(name = "documents_seq", sequenceName = "documents_seq", allocationSize = 1)
    @CsvIgnore
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @ToString.Exclude
    private Group group;

    @Column(length = 20)
    @CsvBindByName
    private String name;

    @Column(columnDefinition = "varchar")
    @CsvBindByName
    private String content;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvIgnore
    private Long version;
}
