package com.github.javiercanillas.jackson.masker;

import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * Mask utilities methods used for masking.
 */
public final class MaskUtils {

    public static final int DEFAULTS_KEEP_LAST_CHARACTERS = 3;
    public static final char DEFAULT_MASK_CHARACTER = '*';

    private MaskUtils() { }

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
