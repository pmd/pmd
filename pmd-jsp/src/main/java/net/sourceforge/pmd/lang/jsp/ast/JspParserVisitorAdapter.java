/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Backwards-compatibility only.
 *
 * @deprecated Use {@link JspVisitorBase}
 */
@Deprecated
@DeprecatedUntil700
public class JspParserVisitorAdapter extends JspVisitorBase<Object, Object> implements JspParserVisitor {

    @Override
    protected Object visitChildren(Node node, Object data) {
        super.visitChildren(node, data);
        return data;
    }

}
