package com.dugx.event.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FavoriteCriteriaTest {

    @Test
    void newFavoriteCriteriaHasAllFiltersNullTest() {
        var favoriteCriteria = new FavoriteCriteria();
        assertThat(favoriteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void favoriteCriteriaFluentMethodsCreatesFiltersTest() {
        var favoriteCriteria = new FavoriteCriteria();

        setAllFilters(favoriteCriteria);

        assertThat(favoriteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void favoriteCriteriaCopyCreatesNullFilterTest() {
        var favoriteCriteria = new FavoriteCriteria();
        var copy = favoriteCriteria.copy();

        assertThat(favoriteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(favoriteCriteria)
        );
    }

    @Test
    void favoriteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var favoriteCriteria = new FavoriteCriteria();
        setAllFilters(favoriteCriteria);

        var copy = favoriteCriteria.copy();

        assertThat(favoriteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(favoriteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var favoriteCriteria = new FavoriteCriteria();

        assertThat(favoriteCriteria).hasToString("FavoriteCriteria{}");
    }

    private static void setAllFilters(FavoriteCriteria favoriteCriteria) {
        favoriteCriteria.id();
        favoriteCriteria.userId();
        favoriteCriteria.eventId();
        favoriteCriteria.distinct();
    }

    private static Condition<FavoriteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getEventId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FavoriteCriteria> copyFiltersAre(FavoriteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getEventId(), copy.getEventId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
