package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketCriteriaTest {

    @Test
    void newTicketCriteriaHasAllFiltersNullTest() {
        var ticketCriteria = new TicketCriteria();
        assertThat(ticketCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketCriteria = new TicketCriteria();

        setAllFilters(ticketCriteria);

        assertThat(ticketCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketCriteriaCopyCreatesNullFilterTest() {
        var ticketCriteria = new TicketCriteria();
        var copy = ticketCriteria.copy();

        assertThat(ticketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCriteria)
        );
    }

    @Test
    void ticketCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketCriteria = new TicketCriteria();
        setAllFilters(ticketCriteria);

        var copy = ticketCriteria.copy();

        assertThat(ticketCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketCriteria = new TicketCriteria();

        assertThat(ticketCriteria).hasToString("TicketCriteria{}");
    }

    private static void setAllFilters(TicketCriteria ticketCriteria) {
        ticketCriteria.id();
        ticketCriteria.status();
        ticketCriteria.checkedIn();
        ticketCriteria.bookingDetailId();
        ticketCriteria.distinct();
    }

    private static Condition<TicketCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCheckedIn()) &&
                condition.apply(criteria.getBookingDetailId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketCriteria> copyFiltersAre(TicketCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCheckedIn(), copy.getCheckedIn()) &&
                condition.apply(criteria.getBookingDetailId(), copy.getBookingDetailId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
