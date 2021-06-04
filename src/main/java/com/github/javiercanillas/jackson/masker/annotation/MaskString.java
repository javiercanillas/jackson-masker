package com.github.javiercanillas.jackson.masker.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.javiercanillas.jackson.masker.MaskUtils;
import com.github.javiercanillas.jackson.masker.ser.MaskedStringSerializer;
import com.github.javiercanillas.jackson.masker.view.Masked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be applied over {@link String} fields to be masked using the view {@link Masked} when using
 * {@link com.fasterxml.jackson.databind.ObjectMapper#writerWithView}. If no view is passed or it's null, data will not
 * be masked (backward compatibility).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = MaskedStringSerializer.class)
public @interface MaskString {
    int DEFAULTS_KEEP_LAST_CHARACTERS = 0;
    char DEFAULTS_MASK_CHARACTER = MaskUtils.DEFAULT_MASK_CHARACTER;

    /**
     * Character to be used for masker. Defaults to {@link MaskString#DEFAULTS_MASK_CHARACTER} if none is set.
     */
    char maskCharacter() default DEFAULTS_MASK_CHARACTER;

    /**
     * Quantity of final characters to left unmasked. Defaults to {@link MaskString#DEFAULTS_KEEP_LAST_CHARACTERS}
     * if none is set. Only a ZERO or POSITIVE values are allowed.
     */
    int keepLastCharacters() default DEFAULTS_KEEP_LAST_CHARACTERS;
}
