/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class PLSQLNameOccurrence implements NameOccurrence {

    private PLSQLNode location;
    private String image;
    private PLSQLNameOccurrence qualifiedName;

    private boolean isMethodOrConstructorInvocation;
    private int argumentCount;

    private static final String THIS = "this";
    private static final String SUPER = "super";

    public PLSQLNameOccurrence(PLSQLNode location, String image) {
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

    public void setNameWhichThisQualifies(PLSQLNameOccurrence qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public PLSQLNameOccurrence getNameForWhichThisIsAQualifier() {
        return qualifiedName;
    }

    public boolean isPartOfQualifiedName() {
        return qualifiedName != null;
    }

    @Override
    public PLSQLNode getLocation() {
        return location;
    }

    public boolean isOnRightHandSide() {
        Node node = location.getParent().getParent().getParent();
        return node instanceof ASTExpression && node.getNumChildren() == 3;
    }

    public boolean isOnLeftHandSide() {
        // I detest this method with every atom of my being
        Node primaryExpression;
        if (location.getParent() instanceof ASTPrimaryExpression) {
            primaryExpression = location.getParent().getParent();
        } else if (location.getParent().getParent() instanceof ASTPrimaryExpression) {
            primaryExpression = location.getParent().getParent().getParent();
        } else {
            throw new RuntimeException(
                    "Found a NameOccurrence that didn't have an ASTPrimaryExpression as parent or grandparent. "
                            + " Node = " + location.getClass().getCanonicalName() + ", Parent = "
                            + location.getParent().getClass().getCanonicalName() + " and grandparent = "
                            + location.getParent().getParent().getClass().getCanonicalName() + " @ line = "
                            + location.getBeginLine() + ", column = " + location.getBeginColumn());
        }

        /*
         * if (isStandAlonePostfix(primaryExpression)) { return true; }
         */

        /*
         * if (!(primaryExpression.getChild(1) instanceof
         * ASTAssignmentOperator)) { return false; }
         */

        /*
         * if (isCompoundAssignment(primaryExpression)) { return false; }
         */

        return primaryExpression.getNumChildren() > 1 && !isPartOfQualifiedName() /* and not is an array type */;
    }

    /*
     * private boolean isCompoundAssignment(Node primaryExpression) { return
     * ((ASTAssignmentOperator) primaryExpression.getChild(1)).isCompound();
     * }
     *
     * private boolean isStandAlonePostfix(Node primaryExpression) { if
     * (!(primaryExpression instanceof ASTPostfixExpression) ||
     * !(primaryExpression.getParent() instanceof ASTStatementExpression)) {
     * return false; }
     *
     * ASTPrimaryPrefix pf = (ASTPrimaryPrefix) ((ASTPrimaryExpression)
     * primaryExpression.getChild(0)).getChild(0); if
     * (pf.usesThisModifier()) { return true; }
     *
     * return thirdChildHasDottedName(primaryExpression); }
     *
     * private boolean thirdChildHasDottedName(Node primaryExpression) { Node
     * thirdChild =
     * primaryExpression.getChild(0).getChild(0).getChild(0); return
     * thirdChild instanceof ASTName && ((ASTName)
     * thirdChild).getImage().indexOf('.') == -1; }
     */

    /**
     * Assert it the occurrence is a self assignment such as:
     * <code>
     * i += 3;
     * </code>
     *
     * @return true, if the occurrence is self-assignment, false, otherwise.
     */
    /*
     * @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop") public
     * boolean isSelfAssignment() { Node l = location; while (true) { Node p =
     * l.getParent(); Node gp = p.getParent(); Node node =
     * gp.getParent(); if (node instanceof ASTPreDecrementExpression || node
     * instanceof ASTPreIncrementExpression || node instanceof
     * ASTPostfixExpression) { return true; }
     *
     * if (hasAssignmentOperator(gp)) { return isCompoundAssignment(gp); }
     *
     * if (hasAssignmentOperator(node)) { return isCompoundAssignment(node); }
     *
     * // deal with extra parenthesis: "(i)++" if (p instanceof ASTPrimaryPrefix
     * && p.getNumChildren() == 1 && gp instanceof ASTPrimaryExpression &&
     * gp.getNumChildren() == 1&& node instanceof ASTExpression &&
     * node.getNumChildren() == 1 && node.getParent() instanceof
     * ASTPrimaryPrefix && node.getParent().getNumChildren() == 1) { l =
     * node; continue; }
     *
     * // catch this.i++ or ++this.i return gp instanceof
     * ASTPreDecrementExpression || gp instanceof ASTPreIncrementExpression ||
     * gp instanceof ASTPostfixExpression; } }
     */

    /*
     * private boolean hasAssignmentOperator(Node node) { if (node instanceof
     * ASTStatementExpression || node instanceof ASTExpression) { if
     * (node.getNumChildren() >= 2 && node.getChild(1) instanceof
     * ASTAssignmentOperator) { return true; } } return false; }
     */

    /**
     * Simply return true is the image is equal to keyword 'this' or 'super'.
     *
     * @return return true if image equal to 'this' or 'super'.
     */
    public boolean isThisOrSuper() {
        return THIS.equals(image) || SUPER.equals(image);
    }

    /**
     * Simply return if the image start with keyword 'this' or 'super'.
     *
     * @return true, if keyword is used, false otherwise.
     */
    /*
     * public boolean useThisOrSuper() { Node node = location.getParent(); if
     * ( node instanceof ASTPrimaryExpression ) { ASTPrimaryExpression
     * primaryExpression = (ASTPrimaryExpression)node; ASTPrimaryPrefix prefix =
     * (ASTPrimaryPrefix) primaryExpression.getChild(0); if ( prefix != null
     * ) { return prefix.usesSuperModifier() || prefix.usesThisModifier(); } }
     * return image.startsWith(THIS_DOT) || image.startsWith(SUPER_DOT); }
     */

    @Override
    public boolean equals(Object o) {
        if (o instanceof PLSQLNameOccurrence) {
            PLSQLNameOccurrence n = (PLSQLNameOccurrence) o;
            return n.getImage().equals(getImage());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getImage().hashCode();
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return getImage() + ":" + location.getReportLocation().startPosToString() + ":" + location.getClass()
                + (this.isMethodOrConstructorInvocation() ? "(method call)" : "");
    }
}
