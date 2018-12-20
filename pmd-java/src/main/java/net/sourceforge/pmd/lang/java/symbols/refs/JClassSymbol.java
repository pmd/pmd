/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;
import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable;


/**
 * Represents a class or interface declaration. This is not exactly a type! This corresponds more
 * closely to a Class instance, meaning it can't be parameterized, etc. Type definitions will use
 * this.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JClassSymbol extends JAccessibleDeclarationSymbol<ASTAnyTypeDeclaration> {

    private final JavaTypeQualifiedName fqcn;

    // TODO this class should present member symbols


    /**
     * Constructor using a class, used to create a reference for a class
     * found by reflection, or a class known at compile-time (eg in {@link JavaLangSymbolTable}).
     *
     * @param clazz          Class represented by this reference
     */
    public JClassSymbol(Class<?> clazz) {
        super(clazz.getModifiers(), clazz.getSimpleName());
        this.fqcn = QualifiedNameFactory.ofClass(clazz);
    }


    /**
     * Constructor using an AST node, probably to be used during scope resolution AST visit.
     *
     * @param node           Node of the declaration
     */
    public JClassSymbol(ASTAnyTypeDeclaration node) {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JClassSymbol that = (JClassSymbol) o;
        return Objects.equals(fqcn, that.fqcn);
    }


    @Override
    public int hashCode() {
        return Objects.hash(fqcn);
    }
}
