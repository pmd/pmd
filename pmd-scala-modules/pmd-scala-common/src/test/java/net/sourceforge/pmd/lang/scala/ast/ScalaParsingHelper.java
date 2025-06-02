/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public final class ScalaParsingHelper extends BaseParsingHelper<ScalaParsingHelper, ASTSource> {

    public static final ScalaParsingHelper DEFAULT = new ScalaParsingHelper(Params.getDefault());

    private ScalaParsingHelper(Params params) {
        super(ScalaLanguageModule.getInstance(), ASTSource.class, params);
    }

    @Override
    protected ScalaParsingHelper clone(Params params) {
        return new ScalaParsingHelper(params);
    }

}
