/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import net.sourceforge.pmd.lang.plsql.PLSQLLanguageModule;

/**
 *
 * @author Stuart Turton sturton@users.sourceforge.net
 */
public class PLSQLLanguage extends AbstractLanguage {
    public PLSQLLanguage() {
        super(PLSQLLanguageModule.NAME, PLSQLLanguageModule.TERSE_NAME, new PLSQLTokenizer(), PLSQLLanguageModule.EXTENSIONS);
    }

    @Override
    public final void setProperties(Properties properties) {
        ((PLSQLTokenizer) getTokenizer()).setProperties(properties);
    }
}
