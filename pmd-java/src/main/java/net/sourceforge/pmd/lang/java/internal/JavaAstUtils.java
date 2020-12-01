/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;

/**
 *
 */
public final class JavaAstUtils {

    private JavaAstUtils() {
        // utility class
    }


    public static boolean isGetterOrSetter(ASTMethodDeclaration node) {
        return isGetter(node) || isSetter(node);
    }


    /** Attempts to determine if the method is a getter. */
    private static boolean isGetter(ASTMethodDeclaration node) {

        if (node.getArity() != 0 || node.isVoid()) {
            return false;
        }

        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();
        if (node.getName().startsWith("get")) {
            return hasField(enclosing, node.getName().substring(3));
        } else if (node.getName().startsWith("is")) {
            return hasField(enclosing, node.getName().substring(2));
        }

        return hasField(enclosing, node.getName());
    }


    /** Attempts to determine if the method is a setter. */
    private static boolean isSetter(ASTMethodDeclaration node) {

        if (node.getArity() != 1 || !node.isVoid()) {
            return false;
        }

        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();

        if (node.getName().startsWith("set")) {
            return hasField(enclosing, node.getName().substring(3));
        }

        return hasField(enclosing, node.getName());
    }


    private static boolean hasField(ASTAnyTypeDeclaration node, String name) {
        for (JFieldSymbol f : node.getSymbol().getDeclaredFields()) {
            String fname = f.getSimpleName();
            if (fname.startsWith("m_") || fname.startsWith("_")) {
                fname = fname.substring(fname.indexOf('_') + 1);
            }
            if (fname.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isConditional(JavaNode ifx) {
        if (ifx instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) ifx).getOperator();
            return op == BinaryOp.CONDITIONAL_AND
                || op == BinaryOp.CONDITIONAL_OR;
        }
        return false;
    }

    public static int numAlternatives(ASTSwitchBranch n) {
        return n.isDefault() ? 1 : n.getLabel().getExprList().count();
    }
}
