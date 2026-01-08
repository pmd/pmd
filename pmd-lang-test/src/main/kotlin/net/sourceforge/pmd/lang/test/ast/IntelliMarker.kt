/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.test.ast


import net.sourceforge.pmd.annotation.InternalApi
import org.junit.jupiter.api.Test

/**
 * This is to trick Intellij into making subclasses executable (because of @Test).
 * But Junit does not use it because of the unsatisfiable condition. This comes from
 * Kotest, but was removed in 4.2.0 without explanation.
 */
interface IntelliMarker {
    @Deprecated("This is not an API")
    fun primer() {
    }

    /**
     * @apiNote Internal API
     */
    @Test
    @InternalApi
    fun dummyTestForIntelliJIntegration() {
    }
}
