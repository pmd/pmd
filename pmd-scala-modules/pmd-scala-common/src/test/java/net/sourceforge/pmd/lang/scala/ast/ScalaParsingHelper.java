/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

public final class ScalaParsingHelper extends BaseParsingHelper<ScalaParsingHelper, ASTSource> {

    public static final ScalaParsingHelper DEFAULT = new ScalaParsingHelper(Params.getDefaultProcess());

    private ScalaParsingHelper(Params params) {
        super(ScalaLanguageModule.NAME, ASTSource.class, params);
    }

    @Override
    protected ScalaParsingHelper clone(Params params) {
        return new ScalaParsingHelper(params);
    }


}
