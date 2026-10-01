package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EventCriteriaTest {

    @Test
    void newEventCriteriaHasAllFiltersNullTest() {
        var eventCriteria = new EventCriteria();
        assertThat(eventCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void eventCriteriaFluentMethodsCreatesFiltersTest() {
        var eventCriteria = new EventCriteria();

        setAllFilters(eventCriteria);

        assertThat(eventCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void eventCriteriaCopyCreatesNullFilterTest() {
        var eventCriteria = new EventCriteria();
        var copy = eventCriteria.copy();

        assertThat(eventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(eventCriteria)
        );
    }

    @Test
    void eventCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var eventCriteria = new EventCriteria();
        setAllFilters(eventCriteria);

        var copy = eventCriteria.copy();

        assertThat(eventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(eventCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var eventCriteria = new EventCriteria();

        assertThat(eventCriteria).hasToString("EventCriteria{}");
    }

    private static void setAllFilters(EventCriteria eventCriteria) {
        eventCriteria.id();
        eventCriteria.title();
        eventCriteria.banner();
        eventCriteria.startTime();
        eventCriteria.endTime();
        eventCriteria.status();
        eventCriteria.createdDate();
        eventCriteria.categoryId();
        eventCriteria.addressId();
        eventCriteria.distinct();
    }

    private static Condition<EventCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getBanner()) &&
                condition.apply(criteria.getStartTime()) &&
                condition.apply(criteria.getEndTime()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getCategoryId()) &&
                condition.apply(criteria.getAddressId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EventCriteria> copyFiltersAre(EventCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getBanner(), copy.getBanner()) &&
                condition.apply(criteria.getStartTime(), copy.getStartTime()) &&
                condition.apply(criteria.getEndTime(), copy.getEndTime()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getCategoryId(), copy.getCategoryId()) &&
                condition.apply(criteria.getAddressId(), copy.getAddressId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
