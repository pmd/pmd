/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.PmdAnalysis;

/**
 * A language that supports {@link PmdAnalysis PMD}.
 *
 * @author Clément Fournier
 */
public interface PmdCapableLanguage extends Language {

    /**
     * Create a new {@link LanguageProcessor} for this language, given
     * a property bundle with configuration. The bundle was created by
     * this instance using {@link #newPropertyBundle()}. It can be assumed
     * that the bundle will never be mutated anymore, and this method
     * takes ownership of it.
     *
     * @param bundle A bundle of properties created by this instance.
     *
     * @return A new language processor
     */
    LanguageProcessor createProcessor(LanguagePropertyBundle bundle);

}
