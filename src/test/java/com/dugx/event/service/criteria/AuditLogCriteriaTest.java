package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AuditLogCriteriaTest {

    @Test
    void newAuditLogCriteriaHasAllFiltersNullTest() {
        var auditLogCriteria = new AuditLogCriteria();
        assertThat(auditLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void auditLogCriteriaFluentMethodsCreatesFiltersTest() {
        var auditLogCriteria = new AuditLogCriteria();

        setAllFilters(auditLogCriteria);

        assertThat(auditLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void auditLogCriteriaCopyCreatesNullFilterTest() {
        var auditLogCriteria = new AuditLogCriteria();
        var copy = auditLogCriteria.copy();

        assertThat(auditLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(auditLogCriteria)
        );
    }

    @Test
    void auditLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var auditLogCriteria = new AuditLogCriteria();
        setAllFilters(auditLogCriteria);

        var copy = auditLogCriteria.copy();

        assertThat(auditLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(auditLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var auditLogCriteria = new AuditLogCriteria();

        assertThat(auditLogCriteria).hasToString("AuditLogCriteria{}");
    }

    private static void setAllFilters(AuditLogCriteria auditLogCriteria) {
        auditLogCriteria.id();
        auditLogCriteria.action();
        auditLogCriteria.tableName();
        auditLogCriteria.recordId();
        auditLogCriteria.createdDate();
        auditLogCriteria.userId();
        auditLogCriteria.distinct();
    }

    private static Condition<AuditLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getTableName()) &&
                condition.apply(criteria.getRecordId()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AuditLogCriteria> copyFiltersAre(AuditLogCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getTableName(), copy.getTableName()) &&
                condition.apply(criteria.getRecordId(), copy.getRecordId()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
