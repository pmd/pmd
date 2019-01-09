/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import java.util.Objects;
import java.util.Optional;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.symbols.internal.JSimpleTypeDeclarationSymbol;


public final class JResolvableClassSymbolImpl extends AbstractDeclarationSymbol<ASTAnyTypeDeclaration> implements net.sourceforge.pmd.lang.java.symbols.internal.JResolvableClassSymbol {

    private final JavaTypeQualifiedName qualifiedName;
    private final Lazy<Optional<JClassSymbolImpl>> myResolvedSymbol;

    /**
     * Builds a symbolic reference to a type using its qualified name.
     *
     * @param fqcn           Fully-qualified class name
     */
    public JResolvableClassSymbolImpl(JavaTypeQualifiedName fqcn) {
        super(fqcn.getClassSimpleName());
        this.qualifiedName = fqcn;
        this.myResolvedSymbol = Lazy.lazy(() -> Optional.ofNullable(fqcn.getType()).map(JClassSymbolImpl::create));
    }


    /**
     * Builds a symbolic reference to a type that has already been resolved.
     *
     * @param alreadyResolved Already resolved type
     */
    public JResolvableClassSymbolImpl(Class<?> alreadyResolved) {
        this(JClassSymbolImpl.create(alreadyResolved));
    }


    /**
     * Builds a resolvable symbol for something that was already resolved.
     *
     * @param alreadyResolved Already resolved symbol
     */
    public JResolvableClassSymbolImpl(JClassSymbolImpl alreadyResolved) {
        super(alreadyResolved.getSimpleName());
        this.qualifiedName = alreadyResolved.getFqcn();
        this.myResolvedSymbol = Lazy.strict(Optional.of(alreadyResolved));
    }


    @Override
    public JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }


    @Override
    public Optional<JClassSymbolImpl> loadClass() {
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
        JResolvableClassSymbolImpl that = (JResolvableClassSymbolImpl) o;
        return Objects.equals(qualifiedName, that.qualifiedName);
    }


    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }
}
