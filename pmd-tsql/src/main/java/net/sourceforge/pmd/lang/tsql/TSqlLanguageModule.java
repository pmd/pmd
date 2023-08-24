/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.tsql.cpd.TSqlTokenizer;

/**
 * @author pguyot@kallisys.net
 */
public class TSqlLanguageModule extends CpdOnlyLanguageModuleBase {
    private static final String ID = "tsql";

    public TSqlLanguageModule() {
        super(LanguageMetadata.withId(ID).name("TSql").extensions("sql"));
    }

    public static TSqlLanguageModule getInstance() {
        return (TSqlLanguageModule) LanguageRegistry.CPD.getLanguageById(ID);
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new TSqlTokenizer();
    }
}
