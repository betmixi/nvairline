package com.dugx.event.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.Organizer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganizerDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String companyName;

    @NotNull
    @Size(max = 50)
    private String taxCode;

    @Lob
    private String description;

    private Boolean verified;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizerDTO)) {
            return false;
        }

        OrganizerDTO organizerDTO = (OrganizerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, organizerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganizerDTO{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", taxCode='" + getTaxCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", verified='" + getVerified() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
