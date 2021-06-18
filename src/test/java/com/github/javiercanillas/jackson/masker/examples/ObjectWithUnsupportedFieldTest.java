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


class ObjectWithUnsupportedFieldTest {

    @Data
    public static class TestObject {

        private Boolean booleanValue;
        @MaskString
        private Boolean sensitiveBoolean;
    }

    private static TestObject buildTestObject(Boolean booleanValue) {
        TestObject obj;
        obj = new TestObject();
        obj.setBooleanValue(booleanValue);
        obj.setSensitiveBoolean(booleanValue);
        return obj;
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(buildTestObject(Boolean.TRUE),
                        "{\"booleanValue\":true,\"sensitiveBoolean\":true}",
                        "{\"booleanValue\":true,\"sensitiveBoolean\":true}"),
                Arguments.of(buildTestObject(Boolean.TRUE),
                        "{\"booleanValue\":true,\"sensitiveBoolean\":true}",
                        "{\"booleanValue\":true,\"sensitiveBoolean\":true}"),
                Arguments.of(buildTestObject(null),
                        "{\"booleanValue\":null,\"sensitiveBoolean\":null}",
                        "{\"booleanValue\":null,\"sensitiveBoolean\":null}")
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
