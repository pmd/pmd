/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

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
}
