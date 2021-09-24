package io.github.javiercanillas.jackson.masker.view;

import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaskedTest {

    @Mock
    SerializerProvider provider;

    @Test
    void nullActiveView() {
        doReturn(null).when(provider).getActiveView();
        assertFalse(Masked.isEnabled(provider));
    }

    @Test
    void notMaskedActiveView() {
        doReturn(Void.class).when(provider).getActiveView();
        assertFalse(Masked.isEnabled(provider));
    }

    @Test
    void maskedActiveView() {
        doReturn(Masked.class).when(provider).getActiveView();
        assertTrue(Masked.isEnabled(provider));
    }

}