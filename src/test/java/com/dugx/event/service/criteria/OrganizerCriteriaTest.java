package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrganizerCriteriaTest {

    @Test
    void newOrganizerCriteriaHasAllFiltersNullTest() {
        var organizerCriteria = new OrganizerCriteria();
        assertThat(organizerCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void organizerCriteriaFluentMethodsCreatesFiltersTest() {
        var organizerCriteria = new OrganizerCriteria();

        setAllFilters(organizerCriteria);

        assertThat(organizerCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void organizerCriteriaCopyCreatesNullFilterTest() {
        var organizerCriteria = new OrganizerCriteria();
        var copy = organizerCriteria.copy();

        assertThat(organizerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(organizerCriteria)
        );
    }

    @Test
    void organizerCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var organizerCriteria = new OrganizerCriteria();
        setAllFilters(organizerCriteria);

        var copy = organizerCriteria.copy();

        assertThat(organizerCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(organizerCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var organizerCriteria = new OrganizerCriteria();

        assertThat(organizerCriteria).hasToString("OrganizerCriteria{}");
    }

    private static void setAllFilters(OrganizerCriteria organizerCriteria) {
        organizerCriteria.id();
        organizerCriteria.companyName();
        organizerCriteria.taxCode();
        organizerCriteria.verified();
        organizerCriteria.userId();
        organizerCriteria.distinct();
    }

    private static Condition<OrganizerCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCompanyName()) &&
                condition.apply(criteria.getTaxCode()) &&
                condition.apply(criteria.getVerified()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrganizerCriteria> copyFiltersAre(OrganizerCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCompanyName(), copy.getCompanyName()) &&
                condition.apply(criteria.getTaxCode(), copy.getTaxCode()) &&
                condition.apply(criteria.getVerified(), copy.getVerified()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
