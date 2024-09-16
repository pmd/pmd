package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.util.ContextedAssertionError;
import org.apache.commons.lang3.exception.DefaultExceptionContext;
import org.apache.commons.lang3.exception.ExceptionContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class ExceptionContextDefaultImplDiffblueTest {
    /**
     * Method under test:
     * {@link ExceptionContextDefaultImpl#addContextValue(String, Object)}
     */
    @Test
    void testAddContextValue() {
        // Arrange
        ContextedAssertionError wrapResult = ContextedAssertionError.wrap(new AssertionError('A'));

        // Act
        ContextedAssertionError actualAddContextValueResult = wrapResult.addContextValue("Label", "Value");

        // Assert
        assertEquals("A\nException Context:\n\t[1:Label=Value]\n---------------------------------",
                wrapResult.getLocalizedMessage());
        assertEquals("A\nException Context:\n\t[1:Label=Value]\n---------------------------------",
                wrapResult.getMessage());
        Set<String> contextLabels = wrapResult.getContextLabels();
        assertEquals(1, contextLabels.size());
        assertTrue(contextLabels.contains("Label"));
        assertSame(wrapResult, actualAddContextValueResult);
    }

    /**
     * Method under test:
     * {@link ExceptionContextDefaultImpl#setContextValue(String, Object)}
     */
    @Test
    void testSetContextValue() {
        // Arrange
        ContextedAssertionError wrapResult = ContextedAssertionError.wrap(new AssertionError('A'));

        // Act
        ExceptionContext actualSetContextValueResult = wrapResult.setContextValue("Label", "Value");

        // Assert
        assertTrue(actualSetContextValueResult instanceof DefaultExceptionContext);
        List<Pair<String, Object>> contextEntries = actualSetContextValueResult.getContextEntries();
        assertEquals(1, contextEntries.size());
        Pair<String, Object> getResult = contextEntries.get(0);
        assertTrue(getResult instanceof ImmutablePair);
        assertEquals("A\nException Context:\n\t[1:Label=Value]\n---------------------------------",
                wrapResult.getLocalizedMessage());
        assertEquals("A\nException Context:\n\t[1:Label=Value]\n---------------------------------",
                wrapResult.getMessage());
        assertEquals("Label", getResult.getKey());
        assertEquals("Label", getResult.getLeft());
        assertEquals("Value", getResult.getRight());
        Set<String> contextLabels = wrapResult.getContextLabels();
        assertEquals(1, contextLabels.size());
        Set<String> contextLabels2 = actualSetContextValueResult.getContextLabels();
        assertEquals(1, contextLabels2.size());
        assertTrue(contextLabels.contains("Label"));
        assertTrue(contextLabels2.contains("Label"));
        assertSame(getResult.getRight(), getResult.getValue());
    }

    /**
     * Method under test:
     * {@link ExceptionContextDefaultImpl#getContextValues(String)}
     */
    @Test
    void testGetContextValues() {
        // Arrange, Act and Assert
        assertTrue(ContextedAssertionError.wrap(new AssertionError('A')).getContextValues("Label").isEmpty());
    }

    /**
     * Method under test:
     * {@link ExceptionContextDefaultImpl#getFirstContextValue(String)}
     */
    @Test
    void testGetFirstContextValue() {
        // Arrange, Act and Assert
        assertNull(ContextedAssertionError.wrap(new AssertionError('A')).getFirstContextValue("Label"));
    }

    /**
     * Method under test: {@link ExceptionContextDefaultImpl#getContextLabels()}
     */
    @Test
    void testGetContextLabels() {
        // Arrange, Act and Assert
        assertTrue(ContextedAssertionError.wrap(new AssertionError('A')).getContextLabels().isEmpty());
    }

    /**
     * Method under test: {@link ExceptionContextDefaultImpl#getContextEntries()}
     */
    @Test
    void testGetContextEntries() {
        // Arrange, Act and Assert
        assertTrue(ContextedAssertionError.wrap(new AssertionError('A')).getContextEntries().isEmpty());
    }

    /**
     * Method under test:
     * {@link ExceptionContextDefaultImpl#getFormattedExceptionMessage(String)}
     */
    @Test
    void testGetFormattedExceptionMessage() {
        // Arrange, Act and Assert
        assertEquals("An error occurred",
                ContextedAssertionError.wrap(new AssertionError('A')).getFormattedExceptionMessage("An error occurred"));
    }
}
