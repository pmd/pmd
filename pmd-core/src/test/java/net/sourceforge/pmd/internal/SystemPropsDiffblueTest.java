package net.sourceforge.pmd.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class SystemPropsDiffblueTest {
    /**
     * Method under test: {@link SystemProps#isErrorRecoveryMode()}
     */
    @Test
    void testIsErrorRecoveryMode() {
        // Arrange, Act and Assert
        assertFalse(SystemProps.isErrorRecoveryMode());
    }
}
