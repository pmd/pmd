/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents either a {@code case} or {@code default} label inside
 * a {@linkplain ASTSwitchStatement switch statement}.
 *
 * <pre>
 *
 * SwitchLabel ::=  "case" {@linkplain ASTExpression Expression} ":"
 *                | "default" ":"
 *
 * </pre>
 */
public class ASTSwitchLabel extends AbstractJavaNode {

    private boolean isDefault;


    @InternalApi
    @Deprecated
    public ASTSwitchLabel(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTSwitchLabel(JavaParser p, int id) {
        super(p, id);
    }


    @InternalApi
    @Deprecated
    public void setDefault() {
        isDefault = true;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
