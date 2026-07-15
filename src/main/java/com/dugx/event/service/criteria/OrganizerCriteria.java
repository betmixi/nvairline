package com.dugx.event.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dugx.event.domain.Organizer} entity. This class is used
 * in {@link com.dugx.event.web.rest.OrganizerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /organizers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganizerCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter companyName;

    private StringFilter taxCode;

    private BooleanFilter verified;

    private LongFilter userId;

    private Boolean distinct;

    public OrganizerCriteria() {}

    public OrganizerCriteria(OrganizerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.companyName = other.optionalCompanyName().map(StringFilter::copy).orElse(null);
        this.taxCode = other.optionalTaxCode().map(StringFilter::copy).orElse(null);
        this.verified = other.optionalVerified().map(BooleanFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrganizerCriteria copy() {
        return new OrganizerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCompanyName() {
        return companyName;
    }

    public Optional<StringFilter> optionalCompanyName() {
        return Optional.ofNullable(companyName);
    }

    public StringFilter companyName() {
        if (companyName == null) {
            setCompanyName(new StringFilter());
        }
        return companyName;
    }

    public void setCompanyName(StringFilter companyName) {
        this.companyName = companyName;
    }

    public StringFilter getTaxCode() {
        return taxCode;
    }

    public Optional<StringFilter> optionalTaxCode() {
        return Optional.ofNullable(taxCode);
    }

    public StringFilter taxCode() {
        if (taxCode == null) {
            setTaxCode(new StringFilter());
        }
        return taxCode;
    }

    public void setTaxCode(StringFilter taxCode) {
        this.taxCode = taxCode;
    }

    public BooleanFilter getVerified() {
        return verified;
    }

    public Optional<BooleanFilter> optionalVerified() {
        return Optional.ofNullable(verified);
    }

    public BooleanFilter verified() {
        if (verified == null) {
            setVerified(new BooleanFilter());
        }
        return verified;
    }

    public void setVerified(BooleanFilter verified) {
        this.verified = verified;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrganizerCriteria that = (OrganizerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(companyName, that.companyName) &&
            Objects.equals(taxCode, that.taxCode) &&
            Objects.equals(verified, that.verified) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyName, taxCode, verified, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganizerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCompanyName().map(f -> "companyName=" + f + ", ").orElse("") +
            optionalTaxCode().map(f -> "taxCode=" + f + ", ").orElse("") +
            optionalVerified().map(f -> "verified=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
