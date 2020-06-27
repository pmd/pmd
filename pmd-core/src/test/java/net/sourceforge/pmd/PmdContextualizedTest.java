/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * A base class for PMD tests that rely on a {@link LanguageRegistry}.
 */
public class PmdContextualizedTest {
    private final LanguageRegistry registry;

    public PmdContextualizedTest() {
        this.registry = LanguageRegistry.STATIC;
    }

    public final LanguageRegistry languageRegistry() {
        return registry;
    }

    public Language dummyLanguage() {
        return registry.getLanguage(DummyLanguageModule.NAME);
    }

    public <T extends Rule> T dummyRule(T rule) {
        rule.setLanguage(dummyLanguage());
        return rule;
    }

    @NonNull
    protected PMDConfiguration newConfiguration() {
        return new PMDConfiguration(languageRegistry());
    }

}

