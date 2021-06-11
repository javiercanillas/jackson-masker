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

import java.util.List;
import java.util.stream.Stream;

class ObjectWithStringListFieldTest {

    @Data
    public static class TestObject {

        private List<String> stringValues;
        @MaskString
        private List<String> sensitiveStrings;
        @MaskString(keepLastCharacters = 6)
        private List<String> sensitiveStringKeepLastCharacters;
        @MaskString(keepLastCharacters = 6, maskCharacter = '#')
        private List<String> sensitiveStringKeepLastCharactersWithCustomMask;
    }
    private static TestObject buildTestObject() {
        return new TestObject();
    }
    private static TestObject buildTestObject(List<String> stringValues) {
        var obj = buildTestObject();
        obj.setSensitiveStrings(stringValues);
        obj.setSensitiveStringKeepLastCharacters(stringValues);
        obj.setSensitiveStringKeepLastCharactersWithCustomMask(stringValues);
        obj.setStringValues(stringValues);
        return obj;
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(buildTestObject(List.of("aabbccdd", "eeffgghh")),
                        "{\"stringValues\":[\"aabbccdd\",\"eeffgghh\"],\"sensitiveStrings\":[\"********\",\"********\"],\"sensitiveStringKeepLastCharacters\":[\"**bbccdd\",\"**ffgghh\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"##bbccdd\",\"##ffgghh\"]}",
                        "{\"stringValues\":[\"aabbccdd\",\"eeffgghh\"],\"sensitiveStrings\":[\"aabbccdd\",\"eeffgghh\"],\"sensitiveStringKeepLastCharacters\":[\"aabbccdd\",\"eeffgghh\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"aabbccdd\",\"eeffgghh\"]}"),
                Arguments.of(buildTestObject(List.of("aabb", "ccdd")),
                        "{\"stringValues\":[\"aabb\",\"ccdd\"],\"sensitiveStrings\":[\"****\",\"****\"],\"sensitiveStringKeepLastCharacters\":[\"aabb\",\"ccdd\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"aabb\",\"ccdd\"]}",
                        "{\"stringValues\":[\"aabb\",\"ccdd\"],\"sensitiveStrings\":[\"aabb\",\"ccdd\"],\"sensitiveStringKeepLastCharacters\":[\"aabb\",\"ccdd\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"aabb\",\"ccdd\"]}"),
                Arguments.of(buildTestObject(),
                        "{\"stringValues\":null,\"sensitiveStrings\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null}",
                        "{\"stringValues\":null,\"sensitiveStrings\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null}")
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
