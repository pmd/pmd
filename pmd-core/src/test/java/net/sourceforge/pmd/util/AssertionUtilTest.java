/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AssertionUtilTest {

    @Test
    void validBinaryNameWithNonAsciiLetters() {
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryName("Sample注文IF"));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryNameNoArray("Sample注文IF"));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryName("com.example.Sample注文IF"));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryName("com.example.Sample注文IF[]"));
    }

    @Test
    void validPackageNameWithNonAsciiLetters() {
        assertTrue(AssertionUtil.isValidJavaPackageName("com.example.注文"));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaPackageName("注文"));
    }

    @Test
    void stillRejectsNamesThatAreNotJavaIdentifiers() {
        assertThrows(IllegalArgumentException.class,
            () -> AssertionUtil.assertValidJavaBinaryName("Sample 注文"));
        assertThrows(IllegalArgumentException.class,
            () -> AssertionUtil.assertValidJavaBinaryName("Sample注文IF;"));
        assertFalse(AssertionUtil.isValidJavaPackageName("com. example"));
    }
}
