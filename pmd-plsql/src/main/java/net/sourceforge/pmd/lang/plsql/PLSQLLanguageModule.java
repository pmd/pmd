/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.plsql.cpd.PLSQLCpdLexer;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class PLSQLLanguageModule extends SimpleLanguageModuleBase {
    static final String ID = "plsql";
    static final String NAME = "PLSQL";

    public PLSQLLanguageModule() {
        super(
            LanguageMetadata.withId(ID)
                            .name(NAME)
                            .extensions(
                                "sql",
                                "trg",  // Triggers
                                "prc", "fnc", // Standalone Procedures and Functions
                                "pld", // Oracle*Forms
                                "pls", "plh", "plb", // Packages
                                "pck", "pks", "pkh", "pkb", // Packages
                                "typ", "tyb", // Object Types
                                "tps", "tpb" // Object Types
                            )
                            .addVersion("11g")
                            .addVersion("12c_Release_1", "12.1")
                            .addVersion("12c_Release_2", "12.2")
                            .addVersion("18c")
                            .addVersion("19c")
                            .addDefaultVersion("21c"),
            new PLSQLHandler()
        );
    }

    public static PLSQLLanguageModule getInstance() {
        return (PLSQLLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }


    @Override
    public LanguagePropertyBundle newPropertyBundle() {
        LanguagePropertyBundle bundle = super.newPropertyBundle();
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS);
        bundle.definePropertyDescriptor(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS);
        return bundle;
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new PLSQLCpdLexer(bundle);
    }
}
