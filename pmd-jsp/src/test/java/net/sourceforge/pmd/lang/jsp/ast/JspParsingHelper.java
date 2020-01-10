/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;

public final class JspParsingHelper extends BaseParsingHelper<JspParsingHelper, ASTCompilationUnit> {

    public static final JspParsingHelper DEFAULT = new JspParsingHelper(Params.getDefaultProcess());

    private JspParsingHelper(Params params) {
        super(JspLanguageModule.NAME, ASTCompilationUnit.class, params);
    }

    @Override
    protected JspParsingHelper clone(Params params) {
        return new JspParsingHelper(params);
    }
}
