/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
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

    private final Set<String> importedMethodsHere = new HashSet<>();
    private boolean allAssertionsOn;

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        importedMethodsHere.clear();
        allAssertionsOn = false;
        for (ASTImportDeclaration importDecl : node.findChildrenOfType(ASTImportDeclaration.class)) {
            if (importDecl.isStatic()) {
                if (importDecl.isImportOnDemand()) {
                    if (isAssertionContainer(importDecl.getImportedName())) {
                        // import static org.junit.Assert.*
                        allAssertionsOn = true;
                    }
                } else {
                    checkImportedAssertion(importDecl.getImportedName());
                }
            }
        }

        super.visit(node, data);
        return null;
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        final boolean isAssertTrue = isAssertionCall(node, "assertTrue");
        final boolean isAssertFalse = isAssertionCall(node, "assertFalse");

        if (isAssertTrue || isAssertFalse) {
            ASTArgumentList args = getNonEmptyArgList(node);
            JavaNode lastArg = getChildRev(args, -1);
            ASTEqualityExpression eq = asEqualityExpr(lastArg);
            if (eq != null) {
                boolean isPositive = isPositiveEqualityExpr(eq) == isAssertTrue;
                final String suggestion;
                if (isNullLiteral(eq.getChild(0))
                    || isNullLiteral(eq.getChild(1))) {
                    // use assertNull/assertNonNull
                    suggestion = isPositive ? "assertNull" : "assertNonNull";
                } else {
                    if (isPrimitive(eq.getChild(0)) || isPrimitive(eq.getChild(1))) {
                        suggestion = isPositive ? "assertEquals" : "assertNotEquals";
                    } else {
                        suggestion = isPositive ? "assertSame" : "assertNotSame";
                    }
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

        boolean isAssertEquals = isAssertionCall(node, "assertEquals");
        boolean isAssertNotEquals = isAssertionCall(node, "assertNotEquals");

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

    private boolean isPrimitive(JavaNode node) {
        if (node instanceof TypeNode) {
            Class<?> t0 = ((TypeNode) node).getType();
            return t0 != null && t0.isPrimitive();
        }
        return false;
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

    private boolean isAssertionCall(JavaNode node, String methodName) {
        if (node instanceof ASTExpression) {
            if (node.getNumChildren() == 1) {
                node = node.getChild(0);
            } else {
                return false;
            }
        }
        if (node.getNumChildren() != 2 || !isCall(node, methodName)) {
            return false; // not a call chain
        }

        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) getChildRev(node, -2);
        return isAssertionMethodName(methodName, (ASTName) prefix.getChild(0));
    }

    private static boolean isPossiblyQualifiedMethodName(String methodName, String possiblyQualifiedName) {
        return methodName.equals(possiblyQualifiedName)
            || possiblyQualifiedName.length() > methodName.length()
            && possiblyQualifiedName.endsWith(methodName)
            && possiblyQualifiedName.charAt(possiblyQualifiedName.length() - methodName.length() - 1) == '.';
    }

    private boolean isAssertionMethodName(String methodName, ASTName location) {
        String possiblyQualifiedName = location.getImage();
        if (methodName.equals(possiblyQualifiedName)) {
            return allAssertionsOn
                || importedMethodsHere.contains(methodName)
                || TypeTestUtil.isA("junit.framework.TestCase", location.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class));
        }
        if (possiblyQualifiedName.length() > methodName.length()
            && possiblyQualifiedName.endsWith(methodName)
            && possiblyQualifiedName.charAt(possiblyQualifiedName.length() - methodName.length() - 1) == '.') {
            return TypeTestUtil.isA("org.junit.jupiter.api.Assertions", location)
                || TypeTestUtil.isA("org.junit.Assert", location);
        }
        return false;
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
        return node != null && node.getImage().equals("==");
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


    private boolean isAssertionContainer(String importedName) {
        return "org.junit.jupiter.api.Assertions".equals(importedName)
            || "org.junit.Assert".equals(importedName);
    }

    private void checkImportedAssertion(String importedName) {
        String stripped = removePrefixOrNull(importedName, "org.junit.jupiter.api.Assertions.");
        if (stripped == null) {
            stripped = removePrefixOrNull(importedName, "org.junit.Assert.");
        }
        if (stripped != null && stripped.indexOf('.') == -1) {
            importedMethodsHere.add(stripped);
        }
    }

    private static String removePrefixOrNull(String str, String prefix) {
        if (str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return null;
    }
}
