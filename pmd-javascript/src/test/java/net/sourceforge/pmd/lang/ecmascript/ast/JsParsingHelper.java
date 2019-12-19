/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;

public final class JsParsingHelper extends BaseParsingHelper<JsParsingHelper, ASTAstRoot> {

    public static final JsParsingHelper DEFAULT = new JsParsingHelper(Params.getDefaultProcess());

    private JsParsingHelper(@NotNull Params params) {
        super(EcmascriptLanguageModule.NAME, ASTAstRoot.class, params);
    }

    @NotNull
    @Override
    protected JsParsingHelper clone(@NotNull Params params) {
        return new JsParsingHelper(params);
    }
}
