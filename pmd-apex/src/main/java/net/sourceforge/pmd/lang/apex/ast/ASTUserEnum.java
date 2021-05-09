/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.compilation.UserEnum;

public final class ASTUserEnum extends BaseApexClass<UserEnum> {

    ASTUserEnum(UserEnum userEnum) {
        super(userEnum);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);

        // when calculateLineNumbers is called, the root node (ASTApexFile) is not available yet
        if (getParent() == null) {
            // For top level enums, the end is the end of file.
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
        } else {
            // For nested enums, look for the position of the last child, which has a real location
            for (int i = getNumChildren() - 1; i >= 0; i--) {
                ApexNode<?> child = getChild(i);
                if (child.hasRealLoc()) {
                    this.endLine = child.getEndLine();
                    this.endColumn = child.getEndColumn();
                    break;
                }
            }
        }
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
