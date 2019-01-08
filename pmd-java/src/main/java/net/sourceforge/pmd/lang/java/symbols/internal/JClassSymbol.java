/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;


/**
 * Represents a class or interface declaration. This is not exactly a type! This corresponds more
 * closely to a Class instance, meaning it can declare type parameters, but not instantiate them,
 * etc. Type definitions will probably use this internally, but they're not equivalent to this.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JClassSymbol
    extends JAccessibleDeclarationSymbol<ASTAnyTypeDeclaration>
    implements JTypeParameterOwnerSymbol, JMaybeStaticSymbol, JMaybeFinalSymbol {

    private final JavaTypeQualifiedName fqcn;
    private final Lazy<List<JTypeParameterSymbol>> myTypeParameters;
    private final Lazy<List<JClassSymbol>> myMemberClasses;

    // TODO this class should present supertype symbols, but that wouldn't work without reflection
    // unless we have a global symbol cache persisted between runs.

    // Also, it needs the qualified name resolver to be resolve the FQCN of the supertypes so we have
    // to be smart w.r.t. the scheduling of the analysis passes.

    /**
     * Constructor using a class, used to create a reference for a class
     * found by reflection, or a class known at compile-time.
     *
     * @param clazz          Class represented by this reference
     */
    private JClassSymbol(Class<?> clazz) {
        super(Objects.requireNonNull(clazz, "Null class is not allowed").getModifiers(),
              clazz.getSimpleName(),
              clazz.getEnclosingClass());
        this.fqcn = QualifiedNameFactory.ofClass(clazz);
        this.myTypeParameters = new Lazy<>(() -> Arrays.stream(clazz.getTypeParameters()).map(tv -> new JTypeParameterSymbol(this, tv)).collect(Collectors.toList()));
        this.myMemberClasses = new Lazy<>(() -> Arrays.stream(clazz.getDeclaredClasses()).map(JClassSymbol::new).collect(Collectors.toList()));
    }

    /**
     * Constructor using an AST node, probably to be used during scope resolution AST visit.
     *
     * @param node           Node of the declaration
     */
    private JClassSymbol(ASTAnyTypeDeclaration node) {
        super(node, getModifiers(node), node.getImage());
        this.fqcn = Objects.requireNonNull(node.getQualifiedName());
        this.myTypeParameters = new Lazy<>(() -> node.getTypeParameters().stream().map(tp -> new JTypeParameterSymbol(this, tp)).collect(Collectors.toList()));
        this.myMemberClasses = new Lazy<>(
            () -> node.findDescendantsOfType(ASTAnyTypeDeclaration.class).stream()
                      // exclude local classes
                      .filter(ASTAnyTypeDeclaration::isNested)
                      .map(JClassSymbol::new)
                      .collect(Collectors.toList())
        );
    }


    JavaTypeQualifiedName getFqcn() {
        return fqcn;
    }


    public Optional<Class<?>> getClassObject() {
        return Optional.ofNullable(fqcn.getType());
    }


    public List<JClassSymbol> getDeclaredClasses() {
        return myMemberClasses.getValue();
    }


    boolean isStrict() {
        return Modifier.isStrict(myModifiers);
    }


    boolean isAbstract() {
        return Modifier.isAbstract(myModifiers);
    }


    @Override
    public boolean isStatic() {
        return Modifier.isStatic(myModifiers);
    }


    @Override
    public boolean isFinal() {
        return Modifier.isFinal(myModifiers);
    }


    public boolean isInterface() {
        return Modifier.isInterface(myModifiers);
    }


    @Override
    public String toString() {
        return "JClassSymbol(" + fqcn.getBinaryName() + ")";
    }


    @Override
    public List<JTypeParameterSymbol> getTypeParameters() {
        return myTypeParameters.getValue();
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

    // these are candidates for

    public static JClassSymbol create(ASTAnyTypeDeclaration node) {
        return new JClassSymbol(node);
    }


    public static JClassSymbol create(Class<?> clazz) {
        return new JClassSymbol(clazz);
    }
}
