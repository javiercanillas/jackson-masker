package com.github.javiercanillas.jackson.masker;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Mask utilities methods used for masking.
 */
@SuppressWarnings("java:S1168")
public final class MaskUtils {
    public static final int DEFAULTS_KEEP_LAST_CHARACTERS = 0;
    public static final char DEFAULT_MASK_CHARACTER = '*';

    private MaskUtils() { }

    /**
     * Mask the argument string set keeping the last digits defined in {@param keepLastCharacters} and replacing the others
     * with {@param maskCharacter}.
     * <br/>
     * For example:  <code>mask(Set.of("hello"), 2, '*')</code> will reproduce <code>***lo</code>
     * @param values set of values of string to be masked.
     * @return if value is null, it will return null, otherwise the masked value result. If value's length is smaller than
     * {@param keepLastCharacters} it will not be masked.
     * @throws IllegalArgumentException if {@param keepLastCharacters} is less than 0.
     */
    public static Set<String> mask(final Set<String> values, final int keepLastCharacters,
                                   final char maskCharacter) {
        if (values == null) {
            return null;
        }

        return mask(values.stream(), keepLastCharacters, maskCharacter)
                .collect(Collectors.toSet());
    }

    /**
     * Mask the argument string list keeping the last digits defined in {@param keepLastCharacters} and replacing the others
     * with {@param maskCharacter}.
     * <br/>
     * For example:  <code>mask(List.of("hello"), 2, '*')</code> will reproduce <code>***lo</code>
     * @param values values of strings to be masked.
     * @return if value is null, it will return null, otherwise the masked value result. If value's length is smaller than
     * {@param keepLastCharacters} it will not be masked.
     * @throws IllegalArgumentException if {@param keepLastCharacters} is less than 0.
     */
    public static List<String> mask(final List<String> values, final int keepLastCharacters,
                                    final char maskCharacter) {
        if (values == null) {
            return null;
        }

        return mask(values.stream(), keepLastCharacters, maskCharacter)
                .collect(Collectors.toList());
    }

    /**
     * Mask the argument string array keeping the last digits defined in {@param keepLastCharacters} and replacing the others
     * with {@param maskCharacter}.
     * <br/>
     * For example:  <code>mask(new String[] { "hello" }, 2, '*')</code> will reproduce <code>***lo</code>
     * @param array array of strings to be masked.
     * @return if value is null, it will return null, otherwise the masked value result. If value's length is smaller than
     * {@param keepLastCharacters} it will not be masked.
     * @throws IllegalArgumentException if {@param keepLastCharacters} is less than 0.
     */
    public static String[] mask(final String[] array, final int keepLastCharacters,
                                final char maskCharacter) {
        if (array == null) {
            return null;
        }

        return mask(Arrays.stream(array), keepLastCharacters, maskCharacter)
                .collect(Collectors.toList()).toArray(new String[array.length]);
    }

    /**
     * Mask the argument string array keeping the last digits defined in {@param keepLastCharacters} and replacing the others
     * with {@param maskCharacter}.
     * <br/>
     * For example:  <code>mask(new String[] { "hello" }, 2, '*')</code> will reproduce <code>***lo</code>
     * @param map array of strings to be masked.
     * @return if value is null, it will return null, otherwise the masked value result. If value's length is smaller than
     * {@param keepLastCharacters} it will not be masked.
     * @throws IllegalArgumentException if {@param keepLastCharacters} is less than 0.
     */
    @SuppressWarnings("java:S1452")
    public static Map<?, String> maskMapValues(Map<?, String> map, int keepLastCharacters, char maskCharacter) {
        if (map == null) {
            return null;
        }

        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> mask(e.getValue(), keepLastCharacters, maskCharacter)));
    }

    private static Stream<String> mask(final Stream<String> stream, final int keepLastCharacters,
                                final char maskCharacter) {
        return stream.map(value -> mask(value, keepLastCharacters, maskCharacter));
    }

    /**
     * Mask the argument string value keeping the last digits defined in {@param keepLastCharacters} and replacing the others
     * with {@param maskCharacter}.
     * <br/>
     * For example:  <code>mask("hello", 2, '*')</code> will reproduce <code>***lo</code>
     * @param value string value to be masked.
     * @return if value is null, it will return null, otherwise the masked value result. If value's length is smaller than
     * {@param keepLastCharacters} it will not be masked.
     * @throws IllegalArgumentException if {@param keepLastCharacters} is less than 0.
     */
    public static String mask(final String value, final int keepLastCharacters,
                              final char maskCharacter) {
        if (value == null) {
            return null;
        }
        if (keepLastCharacters < 0) {
            throw new IllegalArgumentException("Parameter keepLastCharacters cannot be less than Zero.");
        }
        if (value.length() <= keepLastCharacters) {
            return value;
        }

        return repeat(maskCharacter, value.length() - keepLastCharacters)
                + value.substring(value.length() - keepLastCharacters);
    }

    private static String repeat(final char word, final int times) {
        return IntStream.range(0, times).mapToObj(i -> word).collect(Collector.of(StringBuilder::new,
                StringBuilder::append,
                StringBuilder::append,
                StringBuilder::toString));
    }
}
