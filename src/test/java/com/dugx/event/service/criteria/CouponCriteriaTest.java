package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CouponCriteriaTest {

    @Test
    void newCouponCriteriaHasAllFiltersNullTest() {
        var couponCriteria = new CouponCriteria();
        assertThat(couponCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void couponCriteriaFluentMethodsCreatesFiltersTest() {
        var couponCriteria = new CouponCriteria();

        setAllFilters(couponCriteria);

        assertThat(couponCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void couponCriteriaCopyCreatesNullFilterTest() {
        var couponCriteria = new CouponCriteria();
        var copy = couponCriteria.copy();

        assertThat(couponCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(couponCriteria)
        );
    }

    @Test
    void couponCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var couponCriteria = new CouponCriteria();
        setAllFilters(couponCriteria);

        var copy = couponCriteria.copy();

        assertThat(couponCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(couponCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var couponCriteria = new CouponCriteria();

        assertThat(couponCriteria).hasToString("CouponCriteria{}");
    }

    private static void setAllFilters(CouponCriteria couponCriteria) {
        couponCriteria.id();
        couponCriteria.code();
        couponCriteria.discount();
        couponCriteria.startDate();
        couponCriteria.endDate();
        couponCriteria.quantity();
        couponCriteria.eventId();
        couponCriteria.distinct();
    }

    private static Condition<CouponCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getDiscount()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getEventId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CouponCriteria> copyFiltersAre(CouponCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getDiscount(), copy.getDiscount()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getEventId(), copy.getEventId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
