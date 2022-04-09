/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;

public class PlsqlParsingHelper extends BaseParsingHelper<PlsqlParsingHelper, ASTInput> {

    /** This runs all processing stages when parsing. */
    public static final PlsqlParsingHelper DEFAULT = new PlsqlParsingHelper(Params.getDefault());

    private PlsqlParsingHelper(Params params) {
        super(PLSQLLanguageModule.NAME, ASTInput.class, params);
    }

    @Override
    protected PlsqlParsingHelper clone(Params params) {
        return new PlsqlParsingHelper(params);
    }

}

