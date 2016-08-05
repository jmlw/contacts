package com.jmlw.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

import com.jmlw.domain.enumeration.EntityType;

/**
 * A TypeValue.
 */
@Entity
@Table(name = "type_value")
@Document(indexName = "typevalue")
public class TypeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    @NotNull
    private EntityType entityType;

    @Column(name = "value")
    @NotNull
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeValue typeValue = (TypeValue) o;
        if(typeValue.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, typeValue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TypeValue{" +
            "id=" + id +
            ", entityType='" + entityType + "'" +
            ", value='" + value + "'" +
            '}';
    }
}
