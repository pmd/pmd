/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * A reference to a class or interface. This is not a type! Types are compile-time
 * constructs, whereas this corresponds more closely to a Class instance.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JClassReference extends JAccessibleReference<ASTAnyTypeDeclaration> {

    private final Class<?> theClass;


    public JClassReference(JScope declaringScope, Class<?> clazz) {
        super(declaringScope, clazz.getModifiers(), clazz.getSimpleName());
        theClass = clazz;
    }


    public JClassReference(JScope declaringScope, ASTAnyTypeDeclaration node, int modifiers) {
        super(declaringScope, node, modifiers, node.getImage());
        theClass = node.getType();
    }


    public Class<?> getClassObject() {
        return theClass;
    }


    boolean isStrict() {
        return Modifier.isStrict(modifiers);
    }


    boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }


    public final boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }


    public final boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

}
