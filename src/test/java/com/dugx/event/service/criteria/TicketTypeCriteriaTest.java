package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketTypeCriteriaTest {

    @Test
    void newTicketTypeCriteriaHasAllFiltersNullTest() {
        var ticketTypeCriteria = new TicketTypeCriteria();
        assertThat(ticketTypeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketTypeCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketTypeCriteria = new TicketTypeCriteria();

        setAllFilters(ticketTypeCriteria);

        assertThat(ticketTypeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketTypeCriteriaCopyCreatesNullFilterTest() {
        var ticketTypeCriteria = new TicketTypeCriteria();
        var copy = ticketTypeCriteria.copy();

        assertThat(ticketTypeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketTypeCriteria)
        );
    }

    @Test
    void ticketTypeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketTypeCriteria = new TicketTypeCriteria();
        setAllFilters(ticketTypeCriteria);

        var copy = ticketTypeCriteria.copy();

        assertThat(ticketTypeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketTypeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketTypeCriteria = new TicketTypeCriteria();

        assertThat(ticketTypeCriteria).hasToString("TicketTypeCriteria{}");
    }

    private static void setAllFilters(TicketTypeCriteria ticketTypeCriteria) {
        ticketTypeCriteria.id();
        ticketTypeCriteria.name();
        ticketTypeCriteria.price();
        ticketTypeCriteria.quantity();
        ticketTypeCriteria.remaining();
        ticketTypeCriteria.saleStart();
        ticketTypeCriteria.saleEnd();
        ticketTypeCriteria.eventId();
        ticketTypeCriteria.distinct();
    }

    private static Condition<TicketTypeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getRemaining()) &&
                condition.apply(criteria.getSaleStart()) &&
                condition.apply(criteria.getSaleEnd()) &&
                condition.apply(criteria.getEventId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketTypeCriteria> copyFiltersAre(TicketTypeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getRemaining(), copy.getRemaining()) &&
                condition.apply(criteria.getSaleStart(), copy.getSaleStart()) &&
                condition.apply(criteria.getSaleEnd(), copy.getSaleEnd()) &&
                condition.apply(criteria.getEventId(), copy.getEventId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
