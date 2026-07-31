package com.dugx.event.domain;

import com.dugx.event.domain.OrganizerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A Organizer.
 */
@Entity
@Table(name = "organizer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organizer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "company_name", length = 100, nullable = false)
    private String companyName;

    @NotNull
    @Size(max = 50)
    @Column(name = "tax_code", length = 50, nullable = false)
    private String taxCode;

    @Column(name = "description")
    private String description;

    @Column(name = "verified")
    private Boolean verified;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrganizerStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public OrganizerStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizerStatus status) {
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public Organizer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public Organizer companyName(String companyName) {
        this.setCompanyName(companyName);
        return this;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxCode() {
        return this.taxCode;
    }

    public Organizer taxCode(String taxCode) {
        this.setTaxCode(taxCode);
        return this;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getDescription() {
        return this.description;
    }

    public Organizer description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public Organizer verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organizer user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organizer)) {
            return false;
        }
        return getId() != null && getId().equals(((Organizer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organizer{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", taxCode='" + getTaxCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", verified='" + getVerified() + "'" +
            "}";
    }
}
