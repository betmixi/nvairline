package com.dugx.event.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * An Aircraft (loai may bay khai thac chuyen bay).
 */
@Entity
@Table(name = "cinema_room")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Aircraft implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "total_columns")
    private Integer totalColumns;

    @Column(name = "room_type")
    private String roomType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Aircraft id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Aircraft name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalRows() {
        return this.totalRows;
    }

    public Aircraft totalRows(Integer totalRows) {
        this.setTotalRows(totalRows);
        return this;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalColumns() {
        return this.totalColumns;
    }

    public Aircraft totalColumns(Integer totalColumns) {
        this.setTotalColumns(totalColumns);
        return this;
    }

    public void setTotalColumns(Integer totalColumns) {
        this.totalColumns = totalColumns;
    }

    public String getRoomType() {
        return this.roomType;
    }

    public Aircraft roomType(String roomType) {
        this.setRoomType(roomType);
        return this;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Aircraft)) {
            return false;
        }
        return getId() != null && getId().equals(((Aircraft) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Aircraft{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", totalRows=" + getTotalRows() +
            ", totalColumns=" + getTotalColumns() +
            ", roomType='" + getRoomType() + "'" +
            "}";
    }
}
