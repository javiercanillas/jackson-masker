package io.github.javiercanillas.jackson.masker.view;

import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.javiercanillas.jackson.masker.annotation.MaskString;

/**
 * Class used as View for {@link com.fasterxml.jackson.databind.ObjectMapper} to masked fields annotated with
 * {@link MaskString}
 */
@SuppressWarnings({"java:S2094"})
public class Masked {

    private Masked() {
        // to avoid instance creation
    }

    /**
     * Check on the given {@link SerializerProvider} for the {@link Masked} view.
     * @param provider {@link SerializerProvider} instance
     * @return true if {@link Masked} view is found active, otherwise false.
     */
    public static boolean isEnabled(final SerializerProvider provider) {
        return (null != provider.getActiveView() && Masked.class.isAssignableFrom(provider.getActiveView()));
    }
}
