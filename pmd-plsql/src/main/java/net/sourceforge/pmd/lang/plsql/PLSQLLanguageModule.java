/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class PLSQLLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "PLSQL";
    public static final String TERSE_NAME = "plsql";

    public PLSQLLanguageModule() {
        super(
            LanguageMetadata.withId(TERSE_NAME)
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
                            ),
            new PLSQLHandler()
        );
    }
}
