/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Objects;
import java.util.Optional;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;


/**
 * Symbolic version of {@link JClassSymbol}, which doesn't load a type
 * but provides access to its FQCN. It can try building a full type reference,
 * but this may fail. This kind of reference may be used by functions like typeIs() or
 * TypeHelper to test the type in the absence of a complete auxclasspath, but cannot
 * be used properly by type resolution since it needs access to eg supertypes and members.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JResolvableClassSymbol extends AbstractDeclarationSymbol<ASTAnyTypeDeclaration> implements JSimpleTypeDeclarationSymbol<ASTAnyTypeDeclaration> {

    private final JavaTypeQualifiedName qualifiedName;
    private final Lazy<Optional<JClassSymbol>> myResolvedSymbol;

    /**
     * Builds a symbolic reference to a type using its qualified name.
     *
     * @param fqcn           Fully-qualified class name
     */
    public JResolvableClassSymbol(JavaTypeQualifiedName fqcn) {
        super(fqcn.getClassSimpleName());
        this.qualifiedName = fqcn;
        this.myResolvedSymbol = new Lazy<>(() -> Optional.ofNullable(fqcn.getType()).map(JClassSymbol::new));
    }


    /**
     * Builds a symbolic reference to a type that has already been resolved.
     *
     * @param alreadyResolved Already resolved type
     */
    public JResolvableClassSymbol(Class<?> alreadyResolved) {
        super(alreadyResolved.getSimpleName());
        this.qualifiedName = QualifiedNameFactory.ofClass(Objects.requireNonNull(alreadyResolved));
        this.myResolvedSymbol = new Lazy<>(() -> Optional.of(alreadyResolved).map(JClassSymbol::new));
    }


    /**
     * Builds a resolvable symbol for something that was already resolved.
     *
     * @param alreadyResolved Already resolved symbol
     */
    public JResolvableClassSymbol(JClassSymbol alreadyResolved) {
        super(alreadyResolved.getSimpleName());
        this.qualifiedName = alreadyResolved.getFqcn();
        this.myResolvedSymbol = new Lazy<>(Optional.of(alreadyResolved));
    }


    /**
     * Returns the qualified name representing this class.
     *
     * @return a qualified name
     */
    public JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }


    /**
     * Attempts to convert this reference into the richer {@link JClassSymbol}
     * by loading the class. If the class can't be resolved (incomplete classpath),
     * returns an empty optional.
     */
    public Optional<JClassSymbol> loadClass() {
        return myResolvedSymbol.getValue();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JResolvableClassSymbol that = (JResolvableClassSymbol) o;
        return Objects.equals(qualifiedName, that.qualifiedName);
    }


    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }
}
