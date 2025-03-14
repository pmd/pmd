/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public class PlsqlParsingHelper extends BaseParsingHelper<PlsqlParsingHelper, ASTInput> {

    /** This runs all processing stages when parsing. */
    public static final PlsqlParsingHelper DEFAULT = new PlsqlParsingHelper(Params.getDefault());

    private PlsqlParsingHelper(Params params) {
        super(PLSQLLanguageModule.getInstance(), ASTInput.class, params);
    }

    @Override
    protected PlsqlParsingHelper clone(Params params) {
        return new PlsqlParsingHelper(params);
    }

}

