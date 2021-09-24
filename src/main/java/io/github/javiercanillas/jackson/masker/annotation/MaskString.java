package io.github.javiercanillas.jackson.masker.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.javiercanillas.jackson.masker.MaskUtils;
import io.github.javiercanillas.jackson.masker.ser.MaskStringSerializer;
import io.github.javiercanillas.jackson.masker.view.Masked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be applied over fields or methods using {@link String}, array of {@link String}s, {@link java.util.Set},
 * {@link java.util.List} or {@link java.util.Map} fields to have its values masked using the view {@link Masked} when using
 * {@link com.fasterxml.jackson.databind.ObjectMapper#writerWithView}. If no view is passed or it's null, data will not
 * be masked (backward compatibility).
 * <p>
 * <b>Note:</b> On {@link java.util.Map} only values will be mask and not keys. Also, on {@link java.util.Set} can have impact
 * on collection size, since when masking duplication might appear.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@JacksonAnnotationsInside
@JsonSerialize(using = MaskStringSerializer.class)
public @interface MaskString {
    int DEFAULTS_KEEP_LAST_CHARACTERS = MaskUtils.DEFAULTS_KEEP_LAST_CHARACTERS;
    char DEFAULTS_MASK_CHARACTER = MaskUtils.DEFAULT_MASK_CHARACTER;
    int DEFAULTS_KEEP_INITIAL_CHARACTERS = MaskUtils.DEFAULTS_KEEP_INITIAL_CHARACTERS;

    /**
     * Character to be used for masker. Defaults to {@link MaskString#DEFAULTS_MASK_CHARACTER} if none is set.
     * @return char to be used to replace masked positions
     */
    char maskCharacter() default DEFAULTS_MASK_CHARACTER;

    /**
     * Quantity of beginning characters to left unmasked. Defaults to {@link MaskString#DEFAULTS_KEEP_INITIAL_CHARACTERS}
     * if none is set. Only a ZERO or POSITIVE values are allowed.
     * @return quantity of characters to leave unmasked
     */
    int keepInitialCharacters() default DEFAULTS_KEEP_INITIAL_CHARACTERS;

    /**
     * Quantity of final characters to left unmasked. Defaults to {@link MaskString#DEFAULTS_KEEP_LAST_CHARACTERS}
     * if none is set. Only a ZERO or POSITIVE values are allowed.
     * @return quantity of characters to leave unmasked
     */
    int keepLastCharacters() default DEFAULTS_KEEP_LAST_CHARACTERS;
}
