/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.SimpleNode;

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

    public SimpleNode getLocation() {
      return location;
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

        return primaryExpression.jjtGetNumChildren() > 1 && primaryExpression.jjtGetChild(1) instanceof ASTAssignmentOperator;
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
