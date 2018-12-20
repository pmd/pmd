/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;
import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.AccessNode;


/**
 * Represents declarations having access modifiers common to {@link JFieldSymbol},
 * {@link JClassSymbol}, {@link JMethodSymbol}, and {@link JConstructorSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class JAccessibleDeclarationSymbol<N extends Node> extends AbstractDeclarationSymbol<N> {
    protected final int modifiers;

    // TODO this class should present the owner class' symbol and include it in the equals/hashcode!

    JAccessibleDeclarationSymbol(int modifiers, String simpleName) {
        super(simpleName);
        this.modifiers = modifiers;
    }


    JAccessibleDeclarationSymbol(N node, int modifiers, String simpleName) {
        super(node, simpleName);
        this.modifiers = modifiers;
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


    static int accessNodeToModifiers(AccessNode accessNode) {

        /*
        AccessNode:                     java.lang.reflect.Modifier

        int PUBLIC = 0x0001;            0x00000001;
        int PROTECTED = 0x0002;         0x00000004;
        int PRIVATE = 0x0004;           0x00000002;
        int ABSTRACT = 0x0008;          0x00000400;
        int STATIC = 0x0010;            0x00000008;
        int FINAL = 0x0020;             0x00000010;
        int SYNCHRONIZED = 0x0040;      0x00000020;
        int NATIVE = 0x0080;            0x00000100;
        int TRANSIENT = 0x0100;         0x00000080;
        int VOLATILE = 0x0200;          0x00000040;
        int STRICTFP = 0x1000;          0x00000800;
        int DEFAULT = 0x2000;           -----------

        */

        // TODO AccessNode should use the conventions of the standard reflection API

        int result = 0;
        if (accessNode.isPublic()) {
            result |= Modifier.PUBLIC;
        }
        if (accessNode.isPrivate()) {
            result |= Modifier.PRIVATE;
        }
        if (accessNode.isProtected()) {
            result |= Modifier.PROTECTED;
        }
        if (accessNode.isStatic()) {
            result |= Modifier.STATIC;
        }
        if (accessNode.isFinal()) {
            result |= Modifier.FINAL;
        }
        if (accessNode.isSynchronized()) {
            result |= Modifier.SYNCHRONIZED;
        }
        if (accessNode.isVolatile()) {
            result |= Modifier.VOLATILE;
        }
        if (accessNode.isTransient()) {
            result |= Modifier.TRANSIENT;
        }
        if (accessNode.isNative()) {
            result |= Modifier.NATIVE;
        }
        if (accessNode.isAbstract()) {
            result |= Modifier.ABSTRACT;
        }
        if (accessNode.isStrictfp()) {
            result |= Modifier.STRICT;
        }

        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        JAccessibleDeclarationSymbol<?> that = (JAccessibleDeclarationSymbol<?>) o;
        return modifiers == that.modifiers;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), modifiers);
    }
}
