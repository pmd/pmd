/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable;


/**
 * A reference to a class or interface. This is not a type! Types are compile-time
 * constructs, whereas this corresponds more closely to a Class instance.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JClassReference extends JAccessibleReference<ASTAnyTypeDeclaration> {

    private final JavaTypeQualifiedName fqcn;


    /**
     * Constructor using a class, used to create a reference for a class
     * found by reflection, or a class known at compile-time (eg in {@link JavaLangSymbolTable}).
     *
     * @param clazz          Class represented by this reference
     */
    public JClassReference(Class<?> clazz) {
        super(clazz.getModifiers(), clazz.getSimpleName());
        this.fqcn = QualifiedNameFactory.ofClass(clazz);
    }


    /**
     * Constructor using an AST node, probably to be used during scope resolution AST visit.
     *
     * @param node           Node of the declaration
     */
    public JClassReference(ASTAnyTypeDeclaration node) {
        super(node, getModifiers(node), node.getImage());
        this.fqcn = node.getQualifiedName();
    }


    public Class<?> getClassObject() {
        return fqcn.getType();
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


    public boolean isInterface() {
        return Modifier.isInterface(modifiers);
    }


    @Override
    public String toString() {
        return "JClassReference(" + fqcn.getBinaryName() + ")";
    }


    private static int getModifiers(ASTAnyTypeDeclaration declaration) {
        int i = accessNodeToModifiers(declaration);

        if (declaration.getTypeKind().equals(TypeKind.INTERFACE)) {
            i |= Modifier.INTERFACE;
        }
        return i;
    }

}
