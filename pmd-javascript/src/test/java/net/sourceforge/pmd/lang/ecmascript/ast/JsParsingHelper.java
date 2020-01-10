/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;

public final class JsParsingHelper extends BaseParsingHelper<JsParsingHelper, ASTAstRoot> {

    public static final JsParsingHelper DEFAULT = new JsParsingHelper(Params.getDefaultProcess());

    private JsParsingHelper(Params params) {
        super(EcmascriptLanguageModule.NAME, ASTAstRoot.class, params);
    }

    @Override
    protected JsParsingHelper clone(Params params) {
        return new JsParsingHelper(params);
    }
}
