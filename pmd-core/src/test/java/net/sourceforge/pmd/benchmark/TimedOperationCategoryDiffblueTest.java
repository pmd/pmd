package net.sourceforge.pmd.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TimedOperationCategoryDiffblueTest {
    /**
     * Method under test: {@link TimedOperationCategory#displayName()}
     */
    @Test
    void testDisplayName() {
        // Arrange, Act and Assert
        assertEquals("Rule", TimedOperationCategory.RULE.displayName());
    }
}
