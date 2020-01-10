/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;

public final class JspParsingHelper extends BaseParsingHelper<JspParsingHelper, ASTCompilationUnit> {

    public static final JspParsingHelper DEFAULT = new JspParsingHelper(Params.getDefaultProcess());

    private JspParsingHelper(@NotNull Params params) {
        super(JspLanguageModule.NAME, ASTCompilationUnit.class, params);
    }

    @NotNull
    @Override
    protected JspParsingHelper clone(@NotNull Params params) {
        return new JspParsingHelper(params);
    }
}
