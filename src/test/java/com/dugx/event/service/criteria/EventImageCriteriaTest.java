package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EventImageCriteriaTest {

    @Test
    void newEventImageCriteriaHasAllFiltersNullTest() {
        var eventImageCriteria = new EventImageCriteria();
        assertThat(eventImageCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void eventImageCriteriaFluentMethodsCreatesFiltersTest() {
        var eventImageCriteria = new EventImageCriteria();

        setAllFilters(eventImageCriteria);

        assertThat(eventImageCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void eventImageCriteriaCopyCreatesNullFilterTest() {
        var eventImageCriteria = new EventImageCriteria();
        var copy = eventImageCriteria.copy();

        assertThat(eventImageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(eventImageCriteria)
        );
    }

    @Test
    void eventImageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var eventImageCriteria = new EventImageCriteria();
        setAllFilters(eventImageCriteria);

        var copy = eventImageCriteria.copy();

        assertThat(eventImageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(eventImageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var eventImageCriteria = new EventImageCriteria();

        assertThat(eventImageCriteria).hasToString("EventImageCriteria{}");
    }

    private static void setAllFilters(EventImageCriteria eventImageCriteria) {
        eventImageCriteria.id();
        eventImageCriteria.imageUrl();
        eventImageCriteria.eventId();
        eventImageCriteria.distinct();
    }

    private static Condition<EventImageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getEventId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EventImageCriteria> copyFiltersAre(EventImageCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getEventId(), copy.getEventId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
