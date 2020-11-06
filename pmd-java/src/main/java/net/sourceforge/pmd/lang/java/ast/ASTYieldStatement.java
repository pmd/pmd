/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

public class ASTYieldStatement extends AbstractJavaTypeNode {

    ASTYieldStatement(int id) {
        super(id);
    }

    ASTYieldStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        String result = super.getImage();
        if (result == null && hasDescendantOfType(ASTName.class)) {
            result = getFirstDescendantOfType(ASTName.class).getImage();
        }
        return result;
    }


    /** Returns the yielded expression. */
    public ASTExpression getExpr() {
        return (ASTExpression) getChild(0);
    }


    /**
     * @deprecated Use the type of the expression yielded by {@link #getExpr()}
     */
    @Deprecated
    @Override
    public Class<?> getType() {
        return super.getType();
    }

    /**
     * @deprecated Use the type of the expression yielded by {@link #getExpr()}
     */
    @Deprecated
    @Override
    public JavaTypeDefinition getTypeDefinition() {
        return super.getTypeDefinition();
    }

}
