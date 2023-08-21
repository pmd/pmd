/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageVersion;

class JavaLanguageModuleTest {

    @Test
    void java9IsSmallerThanJava10() {
        LanguageVersion java9 = JavaLanguageModule.getInstance().getVersion("9");
        LanguageVersion java10 = JavaLanguageModule.getInstance().getVersion("10");

        assertTrue(java9.compareTo(java10) < 0, "java9 should be smaller than java10");
    }

    @Test
    void previewVersionShouldBeGreaterThanNonPreview() {
        LanguageVersion java20 = JavaLanguageModule.getInstance().getVersion("20");
        LanguageVersion java20p = JavaLanguageModule.getInstance().getVersion("20-preview");

        assertTrue(java20p.compareTo(java20) > 0, "java20-preview should be greater than java20");
    }

    @Test
    void testCompareToVersion() {
        LanguageVersion java9 = JavaLanguageModule.getInstance().getVersion("9");
        assertTrue(java9.compareToVersion("10") < 0, "java9 should be smaller than java10");
    }

    @Test
    void allVersions() {
        List<LanguageVersion> versions = JavaLanguageModule.getInstance().getVersions();
        for (int i = 1; i < versions.size(); i++) {
            LanguageVersion previous = versions.get(i - 1);
            LanguageVersion current = versions.get(i);
            assertTrue(previous.compareTo(current) < 0,
                    "Version " + previous + " should be smaller than " + current);
        }
    }
}
