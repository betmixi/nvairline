package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PaymentCriteriaTest {

    @Test
    void newPaymentCriteriaHasAllFiltersNullTest() {
        var paymentCriteria = new PaymentCriteria();
        assertThat(paymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void paymentCriteriaFluentMethodsCreatesFiltersTest() {
        var paymentCriteria = new PaymentCriteria();

        setAllFilters(paymentCriteria);

        assertThat(paymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void paymentCriteriaCopyCreatesNullFilterTest() {
        var paymentCriteria = new PaymentCriteria();
        var copy = paymentCriteria.copy();

        assertThat(paymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(paymentCriteria)
        );
    }

    @Test
    void paymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var paymentCriteria = new PaymentCriteria();
        setAllFilters(paymentCriteria);

        var copy = paymentCriteria.copy();

        assertThat(paymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(paymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var paymentCriteria = new PaymentCriteria();

        assertThat(paymentCriteria).hasToString("PaymentCriteria{}");
    }

    private static void setAllFilters(PaymentCriteria paymentCriteria) {
        paymentCriteria.id();
        paymentCriteria.method();
        paymentCriteria.transactionCode();
        paymentCriteria.amount();
        paymentCriteria.status();
        paymentCriteria.paymentDate();
        paymentCriteria.bookingId();
        paymentCriteria.distinct();
    }

    private static Condition<PaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMethod()) &&
                condition.apply(criteria.getTransactionCode()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPaymentDate()) &&
                condition.apply(criteria.getBookingId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PaymentCriteria> copyFiltersAre(PaymentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMethod(), copy.getMethod()) &&
                condition.apply(criteria.getTransactionCode(), copy.getTransactionCode()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPaymentDate(), copy.getPaymentDate()) &&
                condition.apply(criteria.getBookingId(), copy.getBookingId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
