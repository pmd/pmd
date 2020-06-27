/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.properties.PropertyFactory;

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


}

