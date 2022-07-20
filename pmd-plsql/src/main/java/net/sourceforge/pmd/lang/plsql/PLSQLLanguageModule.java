/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class PLSQLLanguageModule extends BaseLanguageModule {

    public static final String NAME = "PLSQL";
    public static final String TERSE_NAME = "plsql";

    public PLSQLLanguageModule() {
        super(NAME, null, TERSE_NAME,
                "sql",
                "trg",  // Triggers
                "prc", "fnc", // Standalone Procedures and Functions
                "pld", // Oracle*Forms
                "pls", "plh", "plb", // Packages
                "pck", "pks", "pkh", "pkb", // Packages
                "typ", "tyb", // Object Types
                "tps", "tpb" // Object Types
        );
        addVersion("", new PLSQLHandler(), true);
    }


    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(bundle, new PLSQLHandler());
    }
}
