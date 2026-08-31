/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class KotlinLanguageProcessorTest {

    // --- validateKotlinPath ---

    @Test
    void validateKotlinPathAcceptsKtPath() {
        assertDoesNotThrow(() -> KotlinTypeAwarenessSupport.validateKotlinPath("/path/to/Foo.kt"));
    }

    @Test
    void validateKotlinPathAcceptsSyntheticUnknownPath() {
        // PMD test framework uses "(unknown)" as absPath for synthetic files — must not throw.
        assertDoesNotThrow(() -> KotlinTypeAwarenessSupport.validateKotlinPath("(unknown)"));
    }

    @Test
    void validateKotlinPathThrowsOnEmpty() {
        assertThrows(IllegalStateException.class,
                () -> KotlinTypeAwarenessSupport.validateKotlinPath(""));
    }

    @Test
    void validateKotlinPathThrowsOnNull() {
        assertThrows(IllegalStateException.class,
                () -> KotlinTypeAwarenessSupport.validateKotlinPath(null));
    }
}
