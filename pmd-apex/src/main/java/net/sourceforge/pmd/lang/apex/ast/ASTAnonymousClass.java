/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.compilation.AnonymousClass;

public final class ASTAnonymousClass extends AbstractApexNode<AnonymousClass> {

    ASTAnonymousClass(AnonymousClass anonymousClass) {
        super(anonymousClass);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);

        // For nested anonymous classes, look for the position of the last child, which has a real location
        for (int i = getNumChildren() - 1; i >= 0; i--) {
            ApexNode<?> child = getChild(i);
            if (child.hasRealLoc()) {
                this.endLine = child.getEndLine();
                this.endColumn = child.getEndColumn();
                break;
            }
        }
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getClass().getName();
    }
}
