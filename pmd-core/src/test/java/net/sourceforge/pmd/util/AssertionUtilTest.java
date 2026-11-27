/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
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

    /**
     * Regression test for GHSA-p8c4-pmh9-36vf: very long names must not cause
     * a StackOverflowError in the regex matching.
     */
    @Test
    void veryLongNamesShouldNotStackOverflow() {
        String longName = StringUtils.repeat("a.", 50_000) + "A";
        assertTrue(AssertionUtil.isValidJavaPackageName(longName));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryName(longName));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryName(longName + "[][]"));
        assertDoesNotThrow(() -> AssertionUtil.assertValidJavaBinaryNameNoArray(longName));
        assertFalse(AssertionUtil.isValidJavaPackageName(longName + "."));
    }
}
