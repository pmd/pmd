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


    public boolean usesThisModifier() {
        return this.usesThisModifier;
    }

    public boolean usesSuperModifier() {
        return this.usesSuperModifier;
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
