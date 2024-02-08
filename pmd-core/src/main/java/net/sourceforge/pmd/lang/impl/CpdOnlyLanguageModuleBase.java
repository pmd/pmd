/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

/**
 * Base class for language modules that only support CPD and not PMD.
 *
 * @author Clément Fournier
 */
public abstract class CpdOnlyLanguageModuleBase extends LanguageModuleBase implements CpdCapableLanguage {

    /**
     * Construct a module instance using the given metadata. The metadata must
     * be properly constructed.
     *
     * @throws IllegalStateException If the metadata is invalid (eg missing extensions or name)
     */
    protected CpdOnlyLanguageModuleBase(LanguageMetadata metadata) {
        super(metadata);
    }

    @Override
    public abstract CpdLexer createCpdLexer(LanguagePropertyBundle bundle);
}
