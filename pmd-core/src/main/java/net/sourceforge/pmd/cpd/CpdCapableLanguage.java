/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

/**
 * A language that supports {@link CpdAnalysis CPD}.
 *
 * @author Clément Fournier
 */
public interface CpdCapableLanguage extends Language {


    /**
     * Create a new {@link CpdLexer} for this language, given
     * a property bundle with configuration. The bundle was created by
     * this instance using {@link #newPropertyBundle()}. It can be assumed
     * that the bundle will never be mutated anymore, and this method
     * takes ownership of it.
     *
     * @param bundle A bundle of properties created by this instance.
     *
     * @return A new language processor
     */
    default CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new AnyCpdLexer();
    }
}
