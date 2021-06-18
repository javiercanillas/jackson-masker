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

import java.util.stream.Stream;


class ObjectWithStringFieldTest {

    @Data
    public static class TestObject {

        private String stringValue;
        @MaskString
        private String sensitiveString;
        @MaskString(keepLastCharacters = 6)
        private String sensitiveStringKeepLastCharacters;
        @MaskString(keepInitialCharacters = 1)
        private String sensitiveStringKeepInitialCharacters;
        @MaskString(keepLastCharacters = 6, maskCharacter = '#')
        private String sensitiveStringKeepLastCharactersWithCustomMask;
        @MaskString(keepInitialCharacters = 1, keepLastCharacters = 6, maskCharacter = '#')
        private String sensitiveStringKeepInitialKeepLastCharactersWithCustomMask;
    }

    private static TestObject buildTestObject(String stringValue) {
        TestObject obj;
        obj = new TestObject();
        obj.setSensitiveString(stringValue);
        obj.setSensitiveStringKeepLastCharacters(stringValue);
        obj.setSensitiveStringKeepInitialCharacters(stringValue);
        obj.setSensitiveStringKeepLastCharactersWithCustomMask(stringValue);
        obj.setSensitiveStringKeepInitialKeepLastCharactersWithCustomMask(stringValue);
        obj.setStringValue(stringValue);
        return obj;
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(buildTestObject("aabbccdd"),
                        "{\"stringValue\":\"aabbccdd\",\"sensitiveString\":\"********\",\"sensitiveStringKeepLastCharacters\":\"**bbccdd\",\"sensitiveStringKeepInitialCharacters\":\"a*******\",\"sensitiveStringKeepLastCharactersWithCustomMask\":\"##bbccdd\",\"sensitiveStringKeepInitialKeepLastCharactersWithCustomMask\":\"a#bbccdd\"}",
                        "{\"stringValue\":\"aabbccdd\",\"sensitiveString\":\"aabbccdd\",\"sensitiveStringKeepLastCharacters\":\"aabbccdd\",\"sensitiveStringKeepInitialCharacters\":\"aabbccdd\",\"sensitiveStringKeepLastCharactersWithCustomMask\":\"aabbccdd\",\"sensitiveStringKeepInitialKeepLastCharactersWithCustomMask\":\"aabbccdd\"}"),
                Arguments.of(buildTestObject("aabb"),
                        "{\"stringValue\":\"aabb\",\"sensitiveString\":\"****\",\"sensitiveStringKeepLastCharacters\":\"aabb\",\"sensitiveStringKeepInitialCharacters\":\"a***\",\"sensitiveStringKeepLastCharactersWithCustomMask\":\"aabb\",\"sensitiveStringKeepInitialKeepLastCharactersWithCustomMask\":\"aabb\"}",
                        "{\"stringValue\":\"aabb\",\"sensitiveString\":\"aabb\",\"sensitiveStringKeepLastCharacters\":\"aabb\",\"sensitiveStringKeepInitialCharacters\":\"aabb\",\"sensitiveStringKeepLastCharactersWithCustomMask\":\"aabb\",\"sensitiveStringKeepInitialKeepLastCharactersWithCustomMask\":\"aabb\"}"),
                Arguments.of(buildTestObject(null),
                        "{\"stringValue\":null,\"sensitiveString\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepInitialCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null,\"sensitiveStringKeepInitialKeepLastCharactersWithCustomMask\":null}",
                        "{\"stringValue\":null,\"sensitiveString\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepInitialCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null,\"sensitiveStringKeepInitialKeepLastCharactersWithCustomMask\":null}")
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
