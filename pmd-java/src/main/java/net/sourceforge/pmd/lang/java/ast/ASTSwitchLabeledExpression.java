/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTSwitchLabeledExpression extends AbstractJavaNode implements ASTSwitchLabeledRule {

    @Deprecated
    @InternalApi
    ASTSwitchLabeledExpression(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    ASTSwitchLabeledExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
