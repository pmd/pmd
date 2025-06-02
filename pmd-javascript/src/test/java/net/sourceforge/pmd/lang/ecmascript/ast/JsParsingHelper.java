/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public final class JsParsingHelper extends BaseParsingHelper<JsParsingHelper, ASTAstRoot> {

    public static final JsParsingHelper DEFAULT = new JsParsingHelper(Params.getDefault());

    private JsParsingHelper(Params params) {
        super(EcmascriptLanguageModule.getInstance(), ASTAstRoot.class, params);
    }

    @Override
    protected JsParsingHelper clone(Params params) {
        return new JsParsingHelper(params);
    }
}
