package io.github.javiercanillas.jackson.masker.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.javiercanillas.jackson.masker.annotation.MaskString;
import io.github.javiercanillas.jackson.masker.view.Masked;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Masking {@Link java.util.Set} might be tricky, because it might appear duplications over masked values.
 * Please be aware of this.
 */
class ObjectWithStringSetFieldTest {

    @Data
    public static class TestObject {

        private Set<String> stringValues;
        @MaskString
        private Set<String> sensitiveStrings;
        @MaskString(keepLastCharacters = 6)
        private Set<String> sensitiveStringKeepLastCharacters;
        @MaskString(keepLastCharacters = 6, maskCharacter = '#')
        private Set<String> sensitiveStringKeepLastCharactersWithCustomMask;
    }
    private static TestObject buildTestObject() {
        return new TestObject();
    }
    private static TestObject buildTestObject(Set<String> stringValues) {
        var obj = buildTestObject();
        obj.setSensitiveStrings(stringValues);
        obj.setSensitiveStringKeepLastCharacters(stringValues);
        obj.setSensitiveStringKeepLastCharactersWithCustomMask(stringValues);
        obj.setStringValues(stringValues);
        return obj;
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(buildTestObject(Set.of("aabbccdd", "eeffgghh")),
                        "{\"stringValues\":[\"eeffgghh\",\"aabbccdd\"],\"sensitiveStrings\":[\"********\"],\"sensitiveStringKeepLastCharacters\":[\"**ffgghh\",\"**bbccdd\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"##bbccdd\",\"##ffgghh\"]}",
                        "{\"stringValues\":[\"eeffgghh\",\"aabbccdd\"],\"sensitiveStrings\":[\"eeffgghh\",\"aabbccdd\"],\"sensitiveStringKeepLastCharacters\":[\"eeffgghh\",\"aabbccdd\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"eeffgghh\",\"aabbccdd\"]}"),
                Arguments.of(buildTestObject(Set.of("aabb", "ccdd")),
                        "{\"stringValues\":[\"ccdd\",\"aabb\"],\"sensitiveStrings\":[\"****\"],\"sensitiveStringKeepLastCharacters\":[\"aabb\",\"ccdd\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"aabb\",\"ccdd\"]}",
                        "{\"stringValues\":[\"ccdd\",\"aabb\"],\"sensitiveStrings\":[\"ccdd\",\"aabb\"],\"sensitiveStringKeepLastCharacters\":[\"ccdd\",\"aabb\"],\"sensitiveStringKeepLastCharactersWithCustomMask\":[\"ccdd\",\"aabb\"]}"),
                Arguments.of(buildTestObject(),
                        "{\"stringValues\":null,\"sensitiveStrings\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null}",
                        "{\"stringValues\":null,\"sensitiveStrings\":null,\"sensitiveStringKeepLastCharacters\":null,\"sensitiveStringKeepLastCharactersWithCustomMask\":null}")
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void map(TestObject obj, String maskedStringOutput, String normalStringOutput) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        var expectedMaskObject = mapper.readValue(maskedStringOutput, TestObject.class);
        var expectedNormalObject = mapper.readValue(normalStringOutput, TestObject.class);

        Assertions.assertEquals(expectedMaskObject, mapper.readValue(mapper.writerWithView(Masked.class).writeValueAsString(obj), TestObject.class));
        Assertions.assertEquals(expectedNormalObject, mapper.readValue(mapper.writer().writeValueAsString(obj), TestObject.class));
    }
}
