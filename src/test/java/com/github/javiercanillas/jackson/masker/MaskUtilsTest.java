package com.github.javiercanillas.jackson.masker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MaskUtilsTest {

    private static Stream<Arguments> stringArguments() {
        return Stream.of(
                Arguments.of(null, 0, 0, '*', null),
                Arguments.of(null, 1, 0, '*', null),
                Arguments.of(null, 0, 1, '*', null),
                Arguments.of(null, 1, 1, '*', null),
                Arguments.of("a", 0, 0, '*', "*"),
                Arguments.of("a", 0, 1, '*', "a"),
                Arguments.of("a", 1, 0, '*', "a"),
                Arguments.of("a", 1, 1, '*', "a"),
                Arguments.of("abc", 0, 1, '*', "**c"),
                Arguments.of("abc", 1, 0, '*', "a**"),
                Arguments.of("abc", 1, 1, '*', "a*c"),
                Arguments.of("abcde", 2, 1, '*', "ab**e")
        );
    }

    @ParameterizedTest
    @MethodSource("stringArguments")
    void mask(String value, int keepInitials, int keepLast, char maskChar, String result) {
        assertEquals(result, MaskUtils.mask(value, keepInitials, keepLast, maskChar));
    }

    private static Stream<Arguments> stringArrayArguments() {
        return Stream.of(
                Arguments.of(null, 0, 0, '*', null),
                Arguments.of(new String[0], 0, 0, '*', new String[0]),
                Arguments.of(new String[1], 0, 0, '*', new String[1]),
                Arguments.of(new String[]{"a"}, 0, 1, '*', new String[]{"a"}),
                Arguments.of(new String[]{"a", null}, 0, 1, '*', new String[]{"a", null}),
                Arguments.of(new String[]{"abc", "a"}, 0, 1, '*', new String[]{"**c", "a"})
        );
    }

    @ParameterizedTest
    @MethodSource("stringArrayArguments")
    void mask(String[] value, int keepInitial, int keepLast, char maskChar, String[] result) {
        final String[] masked = MaskUtils.mask(value, keepInitial, keepLast, maskChar);
        if (value == null) {
            assertNull(masked);
        } else {
            assertEquals(result.length, masked.length);
            IntStream.range(0, result.length).forEach(i -> assertEquals(result[i], masked[i]));
        }

    }

    private static Stream<Arguments> stringListArguments() {
        return Stream.of(
                Arguments.of(null, 0, 0, '*', null),
                Arguments.of(List.of(), 0, 0, '*', List.of()),
                Arguments.of(List.of("a"), 0, 1, '*', List.of("a")),
                Arguments.of(List.of("abc", "a"), 0, 1, '*', List.of("**c", "a"))
        );
    }

    @ParameterizedTest
    @MethodSource("stringListArguments")
    void mask(List<String> value, int keepInitial, int keepLast, char maskChar, List<String> result) {
        final List<String> masked = MaskUtils.mask(value, keepInitial, keepLast, maskChar);
        if (value == null) {
            assertNull(masked);
        } else {
            assertEquals(result.size(), masked.size());
            IntStream.range(0, result.size()).forEach(i -> assertEquals(result.get(i), masked.get(i)));
        }

    }

    private static Stream<Arguments> stringSetArguments() {
        return Stream.of(
                Arguments.of(null, 0, 0, '*', null),
                Arguments.of(Set.of(), 0, 0, '*', Set.of()),
                Arguments.of(Set.of("a"), 0, 1, '*', Set.of("a")),
                Arguments.of(Set.of("abc", "a"), 0, 1, '*', Set.of("**c", "a"))
        );
    }

    @ParameterizedTest
    @MethodSource("stringSetArguments")
    void mask(Set<String> value, int keepInitial, int keepLast, char maskChar, Set<String> result) {
        final Set<String> masked = MaskUtils.mask(value, keepInitial, keepLast, maskChar);
        if (value == null) {
            assertNull(masked);
        } else {
            assertEquals(result.size(), masked.size());
            result.forEach(s -> assertTrue(masked.contains(s), String.format("Didn't find %s on masked result", s)));
        }

    }

    private static Stream<Arguments> stringValueMapArguments() {
        return Stream.of(
                Arguments.of(null, 0, 0, '*', null),
                Arguments.of(Map.of(), 0, 0, '*', Map.of()),
                Arguments.of(Map.of("a", "b"), 0, 1, '*', Map.of("a", "b")),
                Arguments.of(Map.of(1, "b"), 0, 1, '*', Map.of(1, "b")),
                Arguments.of(Map.of("a", "bcd"), 0, 1, '*', Map.of("a", "**d")),
                Arguments.of(Map.of(1, "bcd", 3L, "fgh"), 0, 1, '*', Map.of(1, "**d", 3l, "**h")),
                Arguments.of(Map.of(BigDecimal.ONE, "bcd", 3L, "fgh"), 0, 1, '*', Map.of(BigDecimal.ONE, "**d", 3L, "**h"))
        );
    }

    @ParameterizedTest
    @MethodSource("stringValueMapArguments")
    void mask(Map<?, String> value, int keepInitial, int keepLast, char maskChar, Map<?, String> result) {
        final Map<?, String> masked = MaskUtils.maskMapValues(value, keepInitial, keepLast, maskChar);
        if (value == null) {
            assertNull(masked);
        } else {
            assertEquals(result.size(), masked.size());
            result.forEach( (key,val) -> assertEquals(val, masked.get(key)));
        }
    }

    @Test
    void maskIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> MaskUtils.mask("abc", -1, -1, '*'));
        assertThrows(IllegalArgumentException.class, () -> MaskUtils.mask("abc", 0, -1, '*'));
    }
}