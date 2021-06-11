package com.github.javiercanillas.jackson.masker.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javiercanillas.jackson.masker.annotation.MaskString;
import com.github.javiercanillas.jackson.masker.view.Masked;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Masking {@Link java.util.Map} values only, since Keys might be tricky, because it might appear duplications over masked key values.
 */
class ObjectWithStringMapFieldTest {

    @Data
    public static class TestObject {

        private Map<Object, String> stringValues;
        @MaskString
        private Map<Object, String> sensitiveStrings;
        @MaskString(keepLastCharacters = 6)
        private Map<Object, String> sensitiveStringKeepLastCharacters;
        @MaskString(keepLastCharacters = 6, maskCharacter = '#')
        private Map<Object, String> sensitiveStringKeepLastCharactersWithCustomMask;
    }
    private static TestObject buildTestObject() {
        return new TestObject();
    }
    private static TestObject buildTestObject(Map<Object, String> stringValues) {
        var obj = buildTestObject();
        obj.setSensitiveStrings(stringValues);
        obj.setSensitiveStringKeepLastCharacters(stringValues);
        obj.setSensitiveStringKeepLastCharactersWithCustomMask(stringValues);
        obj.setStringValues(stringValues);
        return obj;
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(buildTestObject(Map.of(1, "aabbccdd", 2, "eeffgghh")),
                        "{\"stringValues\":{\"1\":\"aabbccdd\",\"2\":\"eeffgghh\"},\"sensitiveStrings\":{\"1\":\"********\",\"2\":\"********\"},\"sensitiveStringKeepLastCharacters\":{\"1\":\"**bbccdd\",\"2\":\"**ffgghh\"},\"sensitiveStringKeepLastCharactersWithCustomMask\":{\"1\":\"##bbccdd\",\"2\":\"##ffgghh\"}}",
                        ""),
                Arguments.of(buildTestObject(Map.of(1, "aabb", 2, "ccdd")),
                        "{\"stringValues\":{\"1\":\"aabb\",\"2\":\"ccdd\"},\"sensitiveStrings\":{\"1\":\"****\",\"2\":\"****\"},\"sensitiveStringKeepLastCharacters\":{\"1\":\"aabb\",\"2\":\"ccdd\"},\"sensitiveStringKeepLastCharactersWithCustomMask\":{\"1\":\"aabb\",\"2\":\"ccdd\"}}",
                        "{\"stringValues\":{\"1\":\"aabbccdd\",\"2\":\"eeffgghh\"},\"sensitiveStrings\":{\"1\":\"aabbccdd\",\"2\":\"eeffgghh\"},\"sensitiveStringKeepLastCharacters\":{\"1\":\"aabbccdd\",\"2\":\"eeffgghh\"},\"sensitiveStringKeepLastCharactersWithCustomMask\":{\"1\":\"aabbccdd\",\"2\":\"eeffgghh\"}}"),
                Arguments.of(buildTestObject(),
                        "{\"stringValues\":null,\"sensitiveStrings\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null}",
                        "{\"stringValues\":{\"1\":\"aabb\",\"2\":\"ccdd\"},\"sensitiveStrings\":{\"1\":\"aabb\",\"2\":\"ccdd\"},\"sensitiveStringKeepLastCharacters\":{\"1\":\"aabb\",\"2\":\"ccdd\"},\"sensitiveStringKeepLastCharactersWithCustomMask\":{\"1\":\"aabb\",\"2\":\"ccdd\"}}")
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void map(TestObject obj, String maskedStringOutput, String normalStringOutput) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Assertions.assertEquals(maskedStringOutput, mapper.writerWithView(Masked.class).writeValueAsString(obj));
        Assertions.assertEquals(normalStringOutput, mapper.writer().writeValueAsString(obj));
    }
}
