package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CheckInCriteriaTest {

    @Test
    void newCheckInCriteriaHasAllFiltersNullTest() {
        var checkInCriteria = new CheckInCriteria();
        assertThat(checkInCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void checkInCriteriaFluentMethodsCreatesFiltersTest() {
        var checkInCriteria = new CheckInCriteria();

        setAllFilters(checkInCriteria);

        assertThat(checkInCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void checkInCriteriaCopyCreatesNullFilterTest() {
        var checkInCriteria = new CheckInCriteria();
        var copy = checkInCriteria.copy();

        assertThat(checkInCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(checkInCriteria)
        );
    }

    @Test
    void checkInCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var checkInCriteria = new CheckInCriteria();
        setAllFilters(checkInCriteria);

        var copy = checkInCriteria.copy();

        assertThat(checkInCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(checkInCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var checkInCriteria = new CheckInCriteria();

        assertThat(checkInCriteria).hasToString("CheckInCriteria{}");
    }

    private static void setAllFilters(CheckInCriteria checkInCriteria) {
        checkInCriteria.id();
        checkInCriteria.checkInTime();
        checkInCriteria.ticketId();
        checkInCriteria.checkedById();
        checkInCriteria.distinct();
    }

    private static Condition<CheckInCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCheckInTime()) &&
                condition.apply(criteria.getTicketId()) &&
                condition.apply(criteria.getCheckedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CheckInCriteria> copyFiltersAre(CheckInCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCheckInTime(), copy.getCheckInTime()) &&
                condition.apply(criteria.getTicketId(), copy.getTicketId()) &&
                condition.apply(criteria.getCheckedById(), copy.getCheckedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
