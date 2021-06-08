package com.github.javiercanillas.jackson.masker.view;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.javiercanillas.jackson.masker.annotation.MaskString;

/**
 * Class used as View for {@link com.fasterxml.jackson.databind.ObjectMapper} to masked fields annotated with
 * {@link MaskString}
 */
@SuppressWarnings({"java:S2094"})
public class Masked {
    public static boolean isEnabled(final SerializerProvider provider) {
        return (null != provider.getActiveView() && Masked.class.isAssignableFrom(provider.getActiveView()));
    }
}
