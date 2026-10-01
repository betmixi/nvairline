package com.dugx.event.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.Aircraft} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AircraftDTO implements Serializable {

    private Long id;

    @NotBlank
    private String name;

    @Min(value = 0)
    private Integer totalRows;

    @Min(value = 0)
    private Integer totalColumns;

    private String roomType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns(Integer totalColumns) {
        this.totalColumns = totalColumns;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AircraftDTO)) {
            return false;
        }

        AircraftDTO aircraftDTO = (AircraftDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, aircraftDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AircraftDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", totalRows=" + getTotalRows() +
            ", totalColumns=" + getTotalColumns() +
            ", roomType='" + getRoomType() + "'" +
            "}";
    }
}
