/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

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
import net.sourceforge.pmd.lang.java.symbols.internal.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JTypeParameterSymbol;


public final class JClassSymbolImpl
    extends JAccessibleDeclarationSymbolImpl<ASTAnyTypeDeclaration>
    implements JClassSymbol {

    private final JavaTypeQualifiedName fqcn;
    private final Lazy<List<JTypeParameterSymbol>> myTypeParameters;
    private final Lazy<List<JClassSymbol>> myMemberClasses;
    private final TypeKind myTypeKind;

    // TODO this class should present supertype symbols but it needs the symbol
    // table to resolve the supertypes on the AST nodes so we have to be smart
    // w.r.t. the scheduling and the interaction of the analysis passes.
    // That means that this is going to wait for symbol tables to be merged.


    /**
     * Constructor using a class, used to create a reference for a class
     * found by reflection, or a class known at compile-time.
     *
     * @param clazz Class represented by this reference
     */
    private JClassSymbolImpl(Class<?> clazz) {
        super(Objects.requireNonNull(clazz, "Null class is not allowed").getModifiers(),
              clazz.getSimpleName(),
              clazz.getEnclosingClass());
        this.fqcn = QualifiedNameFactory.ofClass(clazz);
        this.myTypeParameters = Lazy.lazy(() -> Arrays.stream(clazz.getTypeParameters()).map(tv -> new JTypeParameterSymbolImpl(this, tv)).collect(Collectors.toList()));
        this.myMemberClasses = Lazy.lazy(() -> Arrays.stream(clazz.getDeclaredClasses()).map(JClassSymbolImpl::new).collect(Collectors.toList()));
        this.myTypeKind = TypeKind.ofClass(clazz);
    }


    /**
     * Constructor using an AST node, probably to be used during scope resolution AST visit.
     *
     * @param node Node of the declaration
     */
    private JClassSymbolImpl(ASTAnyTypeDeclaration node) {
        super(node, accessNodeToModifiers(node), node.getImage());
        this.fqcn = Objects.requireNonNull(node.getQualifiedName());
        this.myTypeParameters = Lazy.lazy(() -> node.getTypeParameters().stream().map(tp -> new JTypeParameterSymbolImpl(this, tp)).collect(Collectors.toList()));
        this.myMemberClasses = Lazy.lazy(
            () -> node.findDescendantsOfType(ASTAnyTypeDeclaration.class).stream()
                      // exclude local classes
                      .filter(ASTAnyTypeDeclaration::isNested)
                      .map(JClassSymbolImpl::new)
                      .collect(Collectors.toList())
        );
        this.myTypeKind = node.getTypeKind();
    }


    @Override
    public JavaTypeQualifiedName getFqcn() {
        return fqcn;
    }


    @Override
    public Optional<Class<?>> getClassObject() {
        return Optional.ofNullable(fqcn.getType());
    }


    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return myMemberClasses.getValue();
    }


    @Override
    public boolean isStrict() {
        return Modifier.isStrict(myModifiers);
    }


    @Override
    public boolean isAbstract() {
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


    @Override
    public TypeKind getTypeKind() {
        return myTypeKind;
    }


    @Override
    public String toString() {
        return "JClassSymbol(" + fqcn.getBinaryName() + ")";
    }


    @Override
    public List<JTypeParameterSymbol> getTypeParameters() {
        return myTypeParameters.getValue();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JClassSymbolImpl that = (JClassSymbolImpl) o;
        return Objects.equals(fqcn, that.fqcn);
    }


    @Override
    public int hashCode() {
        return Objects.hash(fqcn);
    }


    public static JClassSymbolImpl create(ASTAnyTypeDeclaration node) {
        return new JClassSymbolImpl(node);
    }


    public static JClassSymbolImpl create(Class<?> clazz) {
        return new JClassSymbolImpl(clazz);
    }

    // these are candidates for


}
