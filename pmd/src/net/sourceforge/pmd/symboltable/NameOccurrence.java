/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTPostfixExpression;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTName;

public class NameOccurrence {

    private SimpleNode location;
    private String image;
    private NameOccurrence qualifiedName;
    private boolean isMethodOrConstructorInvocation;

    public NameOccurrence(SimpleNode location, String image) {
        this.location = location;
        this.image = image;
    }

    public void setIsMethodOrConstructorInvocation() {
        isMethodOrConstructorInvocation = true;
    }

    public boolean isMethodOrConstructorInvocation() {
        return isMethodOrConstructorInvocation;
    }

    public void setNameWhichThisQualifies(NameOccurrence qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public NameOccurrence getNameForWhichThisIsAQualifier() {
        return qualifiedName;
    }

    public boolean isPartOfQualifiedName() {
        return qualifiedName != null;
    }

    public SimpleNode getLocation() {
        return location;
    }

    public boolean isOnRightHandSide() {
        SimpleNode node = (SimpleNode) location.jjtGetParent().jjtGetParent().jjtGetParent();
        return node instanceof ASTExpression && node.jjtGetNumChildren() == 3;
    }

    public boolean isOnLeftHandSide() {
        SimpleNode primaryExpression;
        if (location.jjtGetParent() instanceof ASTPrimaryExpression) {
            primaryExpression = (SimpleNode) location.jjtGetParent().jjtGetParent();
        } else if (location.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
            primaryExpression = (SimpleNode) location.jjtGetParent().jjtGetParent().jjtGetParent();
        } else {
            throw new RuntimeException("Found a NameOccurrence that didn't have an ASTPrimary Expression as parent or grandparent.  Parent = " + location.jjtGetParent() + " and grandparent = " + location.jjtGetParent().jjtGetParent());
        }

        if (postFixWithExceptions(primaryExpression))  {
            return true;
        }

        if (primaryExpression.jjtGetNumChildren() <= 1) {
            return false;
        }

        if (!(primaryExpression.jjtGetChild(1) instanceof ASTAssignmentOperator)) {
            return false;
        }

        if (isPartOfQualifiedName() /* or is an array type */) {
            return false;
        }

        if (isCompoundAssignment(primaryExpression)) {
            return false;
        }

        return true;
    }

    private boolean isCompoundAssignment(SimpleNode primaryExpression) {
        return ((ASTAssignmentOperator) (primaryExpression.jjtGetChild(1))).isCompound();
    }

    private boolean postFixWithExceptions(SimpleNode primaryExpression) {
        return primaryExpression instanceof ASTPostfixExpression && primaryExpression.jjtGetParent() instanceof ASTStatementExpression && thirdChildHasDottedName(primaryExpression);
    }

    private boolean thirdChildHasDottedName(SimpleNode primaryExpression) {
        return primaryExpression.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTName && ((ASTName)(primaryExpression.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))).getImage().indexOf(".") == -1;
    }

    public Scope getScope() {
        return location.getScope();
    }

    public int getBeginLine() {
        return location.getBeginLine();
    }

    public boolean isThisOrSuper() {
        return image.equals("this") || image.equals("super");
    }

    public boolean equals(Object o) {
        NameOccurrence n = (NameOccurrence) o;
        return n.getImage().equals(getImage());
    }

    public String getImage() {
        return image;
    }

    public int hashCode() {
        return getImage().hashCode();
    }

    public String toString() {
        return getImage() + ":" + location.getBeginLine() + ":" + location.getClass();
    }

}
