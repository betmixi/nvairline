package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BookingDetailCriteriaTest {

    @Test
    void newBookingDetailCriteriaHasAllFiltersNullTest() {
        var bookingDetailCriteria = new BookingDetailCriteria();
        assertThat(bookingDetailCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void bookingDetailCriteriaFluentMethodsCreatesFiltersTest() {
        var bookingDetailCriteria = new BookingDetailCriteria();

        setAllFilters(bookingDetailCriteria);

        assertThat(bookingDetailCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void bookingDetailCriteriaCopyCreatesNullFilterTest() {
        var bookingDetailCriteria = new BookingDetailCriteria();
        var copy = bookingDetailCriteria.copy();

        assertThat(bookingDetailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(bookingDetailCriteria)
        );
    }

    @Test
    void bookingDetailCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var bookingDetailCriteria = new BookingDetailCriteria();
        setAllFilters(bookingDetailCriteria);

        var copy = bookingDetailCriteria.copy();

        assertThat(bookingDetailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(bookingDetailCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var bookingDetailCriteria = new BookingDetailCriteria();

        assertThat(bookingDetailCriteria).hasToString("BookingDetailCriteria{}");
    }

    private static void setAllFilters(BookingDetailCriteria bookingDetailCriteria) {
        bookingDetailCriteria.id();
        bookingDetailCriteria.quantity();
        bookingDetailCriteria.price();
        bookingDetailCriteria.bookingId();
        bookingDetailCriteria.ticketTypeId();
        bookingDetailCriteria.distinct();
    }

    private static Condition<BookingDetailCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getPrice()) &&
                condition.apply(criteria.getBookingId()) &&
                condition.apply(criteria.getTicketTypeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BookingDetailCriteria> copyFiltersAre(
        BookingDetailCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getPrice(), copy.getPrice()) &&
                condition.apply(criteria.getBookingId(), copy.getBookingId()) &&
                condition.apply(criteria.getTicketTypeId(), copy.getTicketTypeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
