/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.Rule;

/**
 * Helper methods.
 */
public final class PmdCoreTestUtils {

    private PmdCoreTestUtils() {
    }

    public static DummyLanguageModule dummyLanguage() {
        return DummyLanguageModule.getInstance();
    }

    public static Dummy2LanguageModule dummyLanguage2() {
        return Dummy2LanguageModule.getInstance();
    }

    public static <T extends Rule> T setDummyLanguage(T rule) {
        rule.setLanguage(dummyLanguage());
        return rule;
    }

    public static LanguageVersion dummyVersion() {
        return dummyLanguage().getDefaultVersion();
    }
}

