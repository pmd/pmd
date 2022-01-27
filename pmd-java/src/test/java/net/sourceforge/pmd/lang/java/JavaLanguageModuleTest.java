/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

public class JavaLanguageModuleTest {
    private Language javaLanguage = LanguageRegistry.getLanguage(JavaLanguageModule.NAME);

    @Test
    public void java9IsSmallerThanJava10() {
        LanguageVersion java9 = javaLanguage.getVersion("9");
        LanguageVersion java10 = javaLanguage.getVersion("10");

        Assert.assertTrue("java9 should be smaller than java10", java9.compareTo(java10) < 0);
    }

    @Test
    public void previewVersionShouldBeGreaterThanNonPreview() {
        LanguageVersion java16 = javaLanguage.getVersion("16");
        LanguageVersion java16p = javaLanguage.getVersion("16-preview");

        Assert.assertTrue("java16-preview should be greater than java16", java16p.compareTo(java16) > 0);
    }

    @Test
    public void testCompareToVersion() {
        LanguageVersion java9 = javaLanguage.getVersion("9");
        Assert.assertTrue("java9 should be smaller than java10", java9.compareToVersion("10") < 0);
    }

    @Test
    public void allVersions() {
        List<LanguageVersion> versions = javaLanguage.getVersions();
        for (int i = 1; i < versions.size(); i++) {
            LanguageVersion previous = versions.get(i - 1);
            LanguageVersion current = versions.get(i);
            Assert.assertTrue("Version " + previous + " should be smaller than " + current,
                    previous.compareTo(current) < 0);
        }
    }
}
