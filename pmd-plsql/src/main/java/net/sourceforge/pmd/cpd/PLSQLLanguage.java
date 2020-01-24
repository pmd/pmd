/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

/**
 *
 * @author Stuart Turton sturton@users.sourceforge.net
 */
public class PLSQLLanguage extends AbstractLanguage {
    public PLSQLLanguage() {
        super("PL/SQL", "plsql", new PLSQLTokenizer(),
                ".sql",
                ".trg", // Triggers
                ".prc", ".fnc", // Standalone Procedures and Functions
                ".pld", // Oracle*Forms
                ".pls", ".plh", ".plb", // Packages
                ".pck", ".pks", ".pkh", ".pkb", // Packages
                ".typ", ".tyb", // Object Types
                ".tps", ".tpb" // Object Types
        );
    }

    @Override
    public final void setProperties(Properties properties) {
        ((PLSQLTokenizer) getTokenizer()).setProperties(properties);
    }
}
