/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

@Deprecated
public class ASTPrimaryPrefix extends AbstractJavaTypeNode {

    private boolean usesThisModifier;
    private boolean usesSuperModifier;

    ASTPrimaryPrefix(int id) {
        super(id);
    }

    ASTPrimaryPrefix(JavaParser p, int id) {
        super(p, id);
    }


    public boolean usesThisModifier() {
        return this.usesThisModifier;
    }

    public boolean usesSuperModifier() {
        return this.usesSuperModifier;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
