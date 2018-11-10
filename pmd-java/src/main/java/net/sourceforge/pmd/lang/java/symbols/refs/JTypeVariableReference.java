/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Represents a type variable. Type variables are reference types, but not class or interface
 * types. They're also not declared with the same node. For those reasons this type of references
 * is distinct from {@link JSymbolicClassReference}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JTypeVariableReference extends AbstractCodeReference<ASTTypeParameter> implements JSimpleTypeReference<ASTTypeParameter> {


    JTypeVariableReference(JScope declaringScope, String simpleName) {
        super(declaringScope, simpleName);
    }


    JTypeVariableReference(JScope declaringScope, ASTTypeParameter node, String simpleName) {
        super(declaringScope, node, simpleName);
    }
}
