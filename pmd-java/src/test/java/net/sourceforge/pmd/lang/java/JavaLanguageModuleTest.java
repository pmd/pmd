/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageVersion;

public class JavaLanguageModuleTest {

    @Test
    public void java9IsSmallerThanJava10() {
        LanguageVersion java9 = JavaLanguageModule.getInstance().getVersion("9");
        LanguageVersion java10 = JavaLanguageModule.getInstance().getVersion("10");

        Assert.assertTrue("java9 should be smaller than java10", java9.compareTo(java10) < 0);
    }

    @Test
    public void previewVersionShouldBeGreaterThanNonPreview() {
        LanguageVersion java18 = JavaLanguageModule.getInstance().getVersion("18");
        LanguageVersion java18p = JavaLanguageModule.getInstance().getVersion("18-preview");

        Assert.assertTrue("java18-preview should be greater than java18", java18p.compareTo(java18) > 0);
    }

    @Test
    public void testCompareToVersion() {
        LanguageVersion java9 = JavaLanguageModule.getInstance().getVersion("9");
        Assert.assertTrue("java9 should be smaller than java10", java9.compareToVersion("10") < 0);
    }

    @Test
    public void allVersions() {
        List<LanguageVersion> versions = JavaLanguageModule.getInstance().getVersions();
        for (int i = 1; i < versions.size(); i++) {
            LanguageVersion previous = versions.get(i - 1);
            LanguageVersion current = versions.get(i);
            Assert.assertTrue("Version " + previous + " should be smaller than " + current,
                    previous.compareTo(current) < 0);
        }
    }
}
