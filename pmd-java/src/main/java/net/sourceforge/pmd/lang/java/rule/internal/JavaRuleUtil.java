/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public final class JavaRuleUtil {

    private JavaRuleUtil() {
        // utility class
    }


    /**
     * Return true if the given expression is enclosed in a zero check.
     * The expression must evaluate to a natural number (ie >= 0), so that
     * {@code e < 1} actually means {@code e == 0}.
     *
     * @param e Expression
     */
    public static boolean isZeroChecked(ASTExpression e) {
        JavaNode parent = e.getParent();
        if (parent instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) parent).getOperator();
            int checkLiteralAtIdx = 1 - e.getIndexInParent();
            JavaNode comparand = parent.getChild(checkLiteralAtIdx);
            int expectedValue;
            if (op == BinaryOp.NE || op == BinaryOp.EQ) {
                // e == 0, e != 0, symmetric
                expectedValue = 0;
            } else if (op == BinaryOp.LT || op == BinaryOp.GE) {
                // e < 1
                // 0 < e
                // e >= 1     (e != 0)
                // 1 >= e     (e == 0 || e == 1)
                // 0 >= e     (e == 0)
                // e >= 0     (true)
                expectedValue = checkLiteralAtIdx;
            } else if (op == BinaryOp.GT || op == BinaryOp.LE) {
                // 1 > e
                // e > 0

                // 1 <= e     (e != 0)
                // e <= 1     (e == 0 || e == 1)
                // e <= 0     (e == 0)
                // 0 <= e     (true)
                expectedValue = 1 - checkLiteralAtIdx;
            } else {
                return false;
            }

            return isIntLit(comparand, expectedValue);
        }
        return false;
    }

    private static boolean isIntLit(JavaNode e, int value) {
        if (e instanceof ASTNumericLiteral) {
            return ((ASTNumericLiteral) e).getValueAsInt() == value;
        }
        return false;
    }

    /**
     * Returns true if the node is a {@link ASTMethodDeclaration} that
     * is a main method.
     */
    public static boolean isMainMethod(JavaNode node) {
        if (node instanceof ASTMethodDeclaration) {
            ASTMethodDeclaration decl = (ASTMethodDeclaration) node;


            return decl.hasModifiers(JModifier.PUBLIC, JModifier.STATIC)
                && "main".equals(decl.getName())
                && decl.isVoid()
                && decl.getArity() == 1
                && TypeTestUtil.isExactlyA(String[].class, decl.getFormalParameters().get(0));
        }
        return false;
    }

    /**
     * Returns true if the node is a utility class, according to this
     * custom definition.
     */
    public static boolean isUtilityClass(ASTAnyTypeDeclaration node) {
        if (node.isInterface() || node.isEnum()) {
            return false;
        }

        ASTClassOrInterfaceDeclaration classNode = (ASTClassOrInterfaceDeclaration) node;

        // A class with a superclass or interfaces should not be considered
        if (classNode.getSuperClassTypeNode() != null
            || !classNode.getSuperInterfaceTypeNodes().isEmpty()) {
            return false;
        }

        // A class without declarations shouldn't be reported
        boolean hasAny = false;

        for (ASTBodyDeclaration declNode : classNode.getDeclarations()) {
            if (declNode instanceof ASTFieldDeclaration
                || declNode instanceof ASTMethodDeclaration) {

                hasAny = isNonPrivate(declNode) && !isMainMethod(declNode);
                if (!((AccessNode) declNode).hasModifiers(JModifier.STATIC)) {
                    return false;
                }

            } else if (declNode instanceof ASTInitializer) {
                if (!((ASTInitializer) declNode).isStatic()) {
                    return false;
                }
            }
        }

        return hasAny;
    }

    private static boolean isNonPrivate(ASTBodyDeclaration decl) {
        return ((AccessNode) decl).getVisibility() != Visibility.V_PRIVATE;
    }

    /**
     * TODO move to a more generic ast util
     *  This is used by the symbol table code, which shouldn't have a dependency on rules
     * @param node
     * @param value
     * @return
     */
    public static boolean isBooleanLit(JavaNode node, boolean value) {
        if (node instanceof ASTBooleanLiteral) {
            return ((ASTBooleanLiteral) node).isTrue() == value;
        }
        return false;
    }
}
