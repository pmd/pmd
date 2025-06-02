/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public final class JspParsingHelper extends BaseParsingHelper<JspParsingHelper, ASTCompilationUnit> {

    public static final JspParsingHelper DEFAULT = new JspParsingHelper(Params.getDefault());

    private JspParsingHelper(Params params) {
        super(JspLanguageModule.getInstance(), ASTCompilationUnit.class, params);
    }

    @Override
    protected JspParsingHelper clone(Params params) {
        return new JspParsingHelper(params);
    }
}
