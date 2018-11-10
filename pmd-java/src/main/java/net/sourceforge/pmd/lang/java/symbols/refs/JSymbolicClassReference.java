/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symbols.scopes.JSymbolTable;


/**
 * Symbolic version of {@link JClassReference}, which doesn't load a type
 * but provides access to its FQCN. It can try building a full type reference,
 * but this may fail. This kind of reference may be used by functions like typeIs() or
 * TypeHelper to test the type, but cannot be used properly by type resolution since
 * it needs access to eg supertypes and members.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JSymbolicClassReference extends AbstractCodeReference<ASTAnyTypeDeclaration> implements JSimpleTypeReference<ASTAnyTypeDeclaration> {

    private final JavaTypeQualifiedName qualifiedName;


    /**
     * Builds a symbolic reference to a type using its qualified name.
     *
     * @param declaringScope Scope to which the class belongs
     * @param fqcn           Fully-qualified class name
     */
    public JSymbolicClassReference(JSymbolTable declaringScope, JavaTypeQualifiedName fqcn) {
        super(declaringScope, fqcn.getClassSimpleName());
        this.qualifiedName = fqcn;
    }


    /**
     * Builds a symbolic reference to a type that has already been resolved.
     *
     * @param declaringScope  Scope to which the class belongs
     * @param alreadyResolved Already resolved type
     */
    public JSymbolicClassReference(JSymbolTable declaringScope, Class<?> alreadyResolved) {
        super(declaringScope, alreadyResolved.getSimpleName());
        this.qualifiedName = QualifiedNameFactory.ofClass(alreadyResolved);
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
     * Attempts to convert this reference into the richer {@link JClassReference}
     * by loading the class. If the class can't be resolved (incomplete classpath),
     * returns an empty optional.
     */
    public Optional<JClassReference> loadClass() {
        Class<?> type = qualifiedName.getType();

        if (type == null) {
            return Optional.empty();
        }

        return Optional.of(new JClassReference(getDeclaringScope(), qualifiedName));
    }

}
