/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 *
 */
public class SimplifiableTestAssertionRule extends AbstractJavaRule {

    private boolean inTestClass;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        boolean oldTestClass = inTestClass;
        inTestClass = isTestClass(node);
        super.visit(node, data);
        inTestClass = oldTestClass;
        return null;
    }

    private static boolean isTestClass(ASTClassOrInterfaceDeclaration p) {
        if (TypeTestUtil.isA("junit.framework.TestCase", p)) {
            return true;
        }

        for (ASTAnyTypeBodyDeclaration decl : p.getDeclarations()) {
            for (ASTAnnotation annot : decl.findChildrenOfType(ASTAnnotation.class)) {
                if (TypeTestUtil.isA("org.junit.Test", annot)
                    || TypeTestUtil.isA("org.junit.jupiter.api.Test", annot)
                    || TypeTestUtil.isA("org.junit.jupiter.api.RepeatedTest", annot)
                    || TypeTestUtil.isA("org.junit.jupiter.api.TestFactory", annot)
                    || TypeTestUtil.isA("org.junit.jupiter.api.TestTemplate", annot)
                    || TypeTestUtil.isA("org.junit.jupiter.params.ParameterizedTest", annot)
                ) {
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (!inTestClass) {
            return super.visit(node, data);
        }
        boolean isAssertTrue = isCall(node, "assertTrue");
        boolean isAssertFalse = isCall(node, "assertFalse");

        if (isAssertTrue || isAssertFalse) {
            ASTArgumentList args = getNonEmptyArgList(node);
            JavaNode lastArg = getChildRev(args, -1);
            ASTEqualityExpression eq = asEqualityExpr(lastArg);
            if (eq != null) {
                boolean isPositive = isPositiveEqualityExpr(eq);
                String suggestion;
                if (isNullLiteral(eq.getChild(0))
                    || isNullLiteral(eq.getChild(1))) {
                    // use assertNull/assertNonNull
                    suggestion = isPositive == isAssertTrue ? "assertNull" : "assertNonNull";
                } else {
                    // use assertSame/assertNotSame
                    suggestion = isPositive == isAssertTrue ? "assertSame" : "assertNotSame";
                }
                addViolation(data, node, suggestion);

            } else {
                JavaNode negatedExprOperand = getNegatedExprOperand(lastArg); // nullable

                if (isCall(negatedExprOperand, "equals")) {
                    //assertTrue(!a.equals(b))
                    String suggestion = isAssertTrue ? "assertNotEquals" : "assertEquals";
                    addViolation(data, node, suggestion);

                } else if (negatedExprOperand != null) {
                    //assertTrue(!something)
                    String suggestion = isAssertTrue ? "assertFalse" : "assertTrue";
                    addViolation(data, node, suggestion);

                } else if (isCall(lastArg, "equals")) {
                    //assertTrue(a.equals(b))
                    String suggestion = isAssertTrue ? "assertEquals" : "assertNotEquals";
                    addViolation(data, node, suggestion);
                }
            }
        }

        boolean isAssertEquals = isCall(node, "assertEquals");
        boolean isAssertNotEquals = isCall(node, "assertNotEquals");

        if (isAssertEquals || isAssertNotEquals) {
            ASTArgumentList argList = getNonEmptyArgList(node);
            if (argList != null && argList.size() >= 2) {
                JavaNode comp0 = getChildRev(argList, -1);
                JavaNode comp1 = getChildRev(argList, -2);
                if (isBooleanLiteral(comp0) ^ isBooleanLiteral(comp1)) {
                    if (isBooleanLiteral(comp1)) {
                        JavaNode tmp = comp0;
                        comp0 = comp1;
                        comp1 = tmp;
                    }
                    // now the literal is in comp0 and the other is some expr
                    if (comp1 instanceof TypeNode && TypeTestUtil.isA(boolean.class, (TypeNode) comp1)) {
                        ASTBooleanLiteral literal = (ASTBooleanLiteral) unwrapLiteral(comp0);
                        String suggestion = literal.isTrue() == isAssertEquals ? "assertTrue" : "assertFalse";
                        addViolation(data, node, suggestion);
                    }
                }
            }
        }

        return super.visit(node, data);
    }

    /**
     * Returns a child with an offset from the end. Eg {@code getChildRev(list, -1)}
     * returns the last child.
     */
    private static JavaNode getChildRev(JavaNode list, int i) {
        assert i < 0 : "Expecting negative offset";
        return list == null ? null : list.getChild(list.getNumChildren() + i);
    }

    /**
     * Checks if the node is a call to a method which has the given name.
     * The receiver expression may be arbitrarily complicated.
     */
    private static boolean isCall(JavaNode node, String methodName) {
        if (node instanceof ASTExpression) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return false;
            }
        }
        if (!(node instanceof ASTPrimaryExpression) || node.getNumChildren() < 2) {
            return false;
        }


        JavaNode prefix = getChildRev(node, -2);
        JavaNode suffix = getChildRev(node, -1);
        if (!(suffix instanceof ASTPrimarySuffix) || !((ASTPrimarySuffix) suffix).isArguments()) {
            return false;
        }
        // we know it's a method call
        if (prefix instanceof ASTPrimaryPrefix
            && prefix.getNumChildren() > 0
            && prefix.getChild(0) instanceof ASTName) {
            String image = prefix.getChild(0).getImage();
            return isPossiblyQualifiedMethodName(methodName, image);
        } else if (prefix instanceof ASTPrimarySuffix) {
            // call chain
            return methodName.equals(prefix.getImage());
        }

        return false;
    }

    private static boolean isPossiblyQualifiedMethodName(String methodName, String possiblyQualifiedName) {
        return methodName.equals(possiblyQualifiedName)
            || possiblyQualifiedName.length() > methodName.length()
            && possiblyQualifiedName.endsWith(methodName)
            && possiblyQualifiedName.charAt(possiblyQualifiedName.length() - methodName.length() - 1) == '.';
    }


    private /*nullable*/ ASTArgumentList getNonEmptyArgList(ASTPrimaryExpression node) {
        ASTPrimarySuffix suffix = node.getFirstChildOfType(ASTPrimarySuffix.class);
        if (suffix != null && suffix.isArguments() && suffix.getArgumentCount() > 0) {
            return (ASTArgumentList) suffix.getChild(0).getChild(0);
        }
        return null;
    }

    private ASTEqualityExpression asEqualityExpr(JavaNode node) {
        if (node instanceof ASTExpression) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return null;
            }
        }
        return node instanceof ASTEqualityExpression ? (ASTEqualityExpression) node
                                                     : null;
    }

    private boolean isPositiveEqualityExpr(ASTEqualityExpression node) {
        return node != null && node.getOperator().equals("==");
    }

    private static JavaNode getNegatedExprOperand(JavaNode node) {
        // /Expression/UnaryExpressionNotPlusMinus[@Image='!']
        //        /PrimaryExpression/PrimaryPrefix
        if (node instanceof ASTExpression) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return null;
            }
        }
        if (node instanceof ASTUnaryExpressionNotPlusMinus
            && "!".equals(((ASTUnaryExpressionNotPlusMinus) node).getOperator())) {
            return node.getChild(0);
        }
        return null;
    }

    private static boolean isNullLiteral(JavaNode node) {
        return unwrapLiteral(node) instanceof ASTNullLiteral;
    }

    private static boolean isBooleanLiteral(JavaNode node) {
        return unwrapLiteral(node) instanceof ASTBooleanLiteral;
    }

    private static JavaNode unwrapLiteral(JavaNode node) {
        if (node instanceof ASTExpression) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return null;
            }
        }
        if (node instanceof ASTPrimaryExpression) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return null;
            }
        }
        if (node instanceof ASTPrimaryPrefix) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return null;
            }
        }
        if (node instanceof ASTLiteral) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return null;
            }
        }
        return node;
    }


}
