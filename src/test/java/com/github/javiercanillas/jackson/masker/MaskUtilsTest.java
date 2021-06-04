package com.github.javiercanillas.jackson.masker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MaskUtilsTest {

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(null, 0, '*', null),
                Arguments.of("a", 0, '*', "*"),
                Arguments.of("a", 1, '*', "a"),
                Arguments.of("abc", 1, '*', "**c")
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void mask(String value, int keepLast, char maskChar, String result) {
        assertEquals(result, MaskUtils.mask(value, keepLast, maskChar));
    }

    @Test
    void maskIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> MaskUtils.mask("abc", -1, '*'));
    }
}