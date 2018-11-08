/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Reference having access modifiers common to {@link JFieldReference},
 * {@link JClassReference}, {@link JMethodReference}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class JAccessibleReference<N extends Node> extends AbstractCodeReference<N> {
    JAccessibleReference(JScope declaringScope, int modifiers, String simpleName) {
        super(declaringScope, modifiers, simpleName);
    }


    JAccessibleReference(JScope declaringScope, N node, int modifiers, String simpleName) {
        super(declaringScope, node, modifiers, simpleName);
    }


    public final boolean isPublic() {
        return Modifier.isPublic(modifiers);
    }


    public final boolean isPrivate() {
        return Modifier.isPrivate(modifiers);
    }


    public final boolean isProtected() {
        return Modifier.isProtected(modifiers);
    }


    public final boolean isPackagePrivate() {
        return (modifiers & (Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC)) == 0;
    }



}
