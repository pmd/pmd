/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.tsql.cpd.TSqlTokenizer;

/**
 * @author pguyot@kallisys.net
 */
public class TSqlLanguageModule extends CpdOnlyLanguageModuleBase {

    public TSqlLanguageModule() {
        super(LanguageMetadata.withId("tsql").name("TSql").extensions("sql"));
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new TSqlTokenizer();
    }
}
