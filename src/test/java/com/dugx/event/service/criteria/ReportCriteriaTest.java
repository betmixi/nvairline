package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReportCriteriaTest {

    @Test
    void newReportCriteriaHasAllFiltersNullTest() {
        var reportCriteria = new ReportCriteria();
        assertThat(reportCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void reportCriteriaFluentMethodsCreatesFiltersTest() {
        var reportCriteria = new ReportCriteria();

        setAllFilters(reportCriteria);

        assertThat(reportCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void reportCriteriaCopyCreatesNullFilterTest() {
        var reportCriteria = new ReportCriteria();
        var copy = reportCriteria.copy();

        assertThat(reportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(reportCriteria)
        );
    }

    @Test
    void reportCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var reportCriteria = new ReportCriteria();
        setAllFilters(reportCriteria);

        var copy = reportCriteria.copy();

        assertThat(reportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(reportCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var reportCriteria = new ReportCriteria();

        assertThat(reportCriteria).hasToString("ReportCriteria{}");
    }

    private static void setAllFilters(ReportCriteria reportCriteria) {
        reportCriteria.id();
        reportCriteria.status();
        reportCriteria.createdDate();
        reportCriteria.userId();
        reportCriteria.eventId();
        reportCriteria.distinct();
    }

    private static Condition<ReportCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getEventId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReportCriteria> copyFiltersAre(ReportCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getEventId(), copy.getEventId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
