/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class JavaNameOccurrence implements NameOccurrence {

    private JavaNode location;
    private String image;
    private NameOccurrence qualifiedName;

    private boolean isMethodOrConstructorInvocation;
    private int argumentCount;

    private final static String THIS = "this";
    private final static String SUPER = "super";

    private final static String THIS_DOT = "this.";
    private final static String SUPER_DOT = "super.";

    public JavaNameOccurrence(JavaNode location, String image) {
        this.location = location;
        this.image = image;
    }

    public void setIsMethodOrConstructorInvocation() {
        isMethodOrConstructorInvocation = true;
    }

    public void setArgumentCount(int count) {
        argumentCount = count;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    public boolean isMethodOrConstructorInvocation() {
        return isMethodOrConstructorInvocation;
    }
    public boolean isMethodReference() {
        return location instanceof ASTMethodReference;
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

    public JavaNode getLocation() {
        return location;
    }

    public boolean isOnRightHandSide() {
	Node node = location.jjtGetParent().jjtGetParent().jjtGetParent();
        return node instanceof ASTExpression && node.jjtGetNumChildren() == 3;
    }


    public boolean isOnLeftHandSide() {
        // I detest this method with every atom of my being
	Node primaryExpression;
        if (location.jjtGetParent() instanceof ASTPrimaryExpression) {
            primaryExpression = location.jjtGetParent().jjtGetParent();
        } else if (location.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
            primaryExpression = location.jjtGetParent().jjtGetParent().jjtGetParent();
        } else {
            throw new RuntimeException("Found a NameOccurrence that didn't have an ASTPrimary Expression as parent or grandparent.  Parent = " + location.jjtGetParent() + " and grandparent = " + location.jjtGetParent().jjtGetParent());
        }

        if (isStandAlonePostfix(primaryExpression)) {
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

    private boolean isCompoundAssignment(Node primaryExpression) {
        return ((ASTAssignmentOperator) primaryExpression.jjtGetChild(1)).isCompound();
    }

    private boolean isStandAlonePostfix(Node primaryExpression) {
        if (!(primaryExpression instanceof ASTPostfixExpression) || !(primaryExpression.jjtGetParent() instanceof ASTStatementExpression)) {
            return false;
        }

        ASTPrimaryPrefix pf = (ASTPrimaryPrefix) ((ASTPrimaryExpression) primaryExpression.jjtGetChild(0)).jjtGetChild(0);
        if (pf.usesThisModifier()) {
            return true;
        }

        return thirdChildHasDottedName(primaryExpression);
    }

    private boolean thirdChildHasDottedName(Node primaryExpression) {
        Node thirdChild = primaryExpression.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        return thirdChild instanceof ASTName && ((ASTName) thirdChild).getImage().indexOf('.') == -1;
    }

    /**
     * Assert it the occurrence is a self assignment such as:
     * <code>
     * 		i += 3;
     * </code>
     *
     * @return true, if the occurrence is self-assignment, false, otherwise.
     */
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    public boolean isSelfAssignment() {
        Node l = location;
        while (true) {
            Node p = l.jjtGetParent();
            Node gp = p.jjtGetParent();
            Node node = gp.jjtGetParent();
            if (node instanceof ASTPreDecrementExpression || node instanceof ASTPreIncrementExpression || node instanceof ASTPostfixExpression) {
                return true;
            }

            if (hasAssignmentOperator(gp)) {
                return isCompoundAssignment(gp);
            }

            if (hasAssignmentOperator(node)) {
                return isCompoundAssignment(node);
            }

            // deal with extra parenthesis: "(i)++"
            if (p instanceof ASTPrimaryPrefix && p.jjtGetNumChildren() == 1 &&
                    gp instanceof ASTPrimaryExpression && gp.jjtGetNumChildren() == 1&&
                    node instanceof ASTExpression && node.jjtGetNumChildren() == 1 &&
                    node.jjtGetParent() instanceof ASTPrimaryPrefix && node.jjtGetParent().jjtGetNumChildren() == 1) {
                l = node;
                continue;
            }

            // catch this.i++ or ++this.i
            return gp instanceof ASTPreDecrementExpression || gp instanceof ASTPreIncrementExpression || gp instanceof ASTPostfixExpression;
        }
    }

    private boolean hasAssignmentOperator(Node node) {
        if (node instanceof ASTStatementExpression || node instanceof ASTExpression) {
            if (node.jjtGetNumChildren() >= 2 && node.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simply return true is the image is equal to keyword 'this' or 'super'.
     *
     * @return return true if image equal to 'this' or 'super'.
     */
    public boolean isThisOrSuper() {
        return image != null && (image.equals(THIS) || image.equals(SUPER));
    }

    /**
     * Simply return if the image start with keyword 'this' or 'super'.
     *
     * @return true, if keyword is used, false otherwise.
     */
    public boolean useThisOrSuper() {
		Node node = location.jjtGetParent();
		if ( node instanceof ASTPrimaryExpression ) {
			ASTPrimaryExpression primaryExpression = (ASTPrimaryExpression)node;
			ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) primaryExpression.jjtGetChild(0);
			if ( prefix != null ) {
			    return prefix.usesSuperModifier() || prefix.usesThisModifier();
			}
		}
    	return image.startsWith(THIS_DOT) || image.startsWith(SUPER_DOT);
    }

    @Override
    public boolean equals(Object o) {
    	if (o instanceof JavaNameOccurrence) {
    		JavaNameOccurrence n = (JavaNameOccurrence) o;
    		return n.getImage().equals(getImage());
    		}
    	return false;
    }

    @Override
    public int hashCode() {
        return getImage().hashCode();
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return getImage() + ":" + location.getBeginLine() + ":" + location.getClass() + (this.isMethodOrConstructorInvocation() ? "(method call)" : "");
    }
}
