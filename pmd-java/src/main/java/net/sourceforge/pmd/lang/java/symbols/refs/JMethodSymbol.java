/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;


/**
 * Reference to a method.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JMethodSymbol extends JAccessibleDeclarationSymbol<ASTMethodDeclaration> {

    private final boolean isDefault;


    /**
     * Constructor for methods found through reflection.
     *
     * @param method         Method for which to create a reference
     */
    public JMethodSymbol(Method method) {
        super(method.getModifiers(), method.getName());
        this.isDefault = method.isDefault();
    }


    /**
     * Constructor using the AST node.
     *
     * @param node           Node representing the method declaration
     */
    public JMethodSymbol(ASTMethodDeclaration node) {
        super(node, getModifiers(node), node.getMethodName());
        this.isDefault = node.isDefault();
    }


    boolean isSynchronized() {
        return Modifier.isSynchronized(modifiers);
    }


    boolean isNative() {
        return Modifier.isNative(modifiers);
    }


    boolean isDefault() {
        return isDefault;
    }


    boolean isStrict() {
        return Modifier.isStrict(modifiers);
    }


    boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }


    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }


    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }


    boolean isVarargs() {
        // Modifier.TRANSIENT is identical to the bit mask used for varargs
        // the reflect API uses that because they can never occur together I guess
        return (modifiers & Modifier.TRANSIENT) != 0;
    }


    private static int getModifiers(ASTMethodDeclaration declaration) {
        int i = accessNodeToModifiers(declaration);

        if (declaration.isVarargs()) {
            i |= Modifier.TRANSIENT;
        }
        return i;
    }

}
