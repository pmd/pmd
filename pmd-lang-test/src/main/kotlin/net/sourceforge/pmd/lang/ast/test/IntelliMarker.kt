/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test


import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * This is to trick Intellij into making subclasses executable (because of @TestFactory).
 * But Junit does not use it because of the unsatisfiable condition. This comes from
 * Kotest, but was removed in 4.2.0 without explanation.
 */
interface IntelliMarker {
    @EnabledIfSystemProperty(named = "wibble", matches = "wobble")
    @TestFactory
    fun primer() {
    }
}
