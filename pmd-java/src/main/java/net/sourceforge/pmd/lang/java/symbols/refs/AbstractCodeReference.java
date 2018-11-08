/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;
import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractCodeReference<N extends Node> implements JCodeReference<N> {

    protected final int modifiers;
    private final JScope declaringScope;
    private final String simpleName;
    private N boundNode;


    AbstractCodeReference(JScope declaringScope, int modifiers, String simpleName) {
        this.declaringScope = declaringScope;
        this.modifiers = modifiers;
        this.simpleName = simpleName;
    }


    AbstractCodeReference(JScope declaringScope, N node, int modifiers, String simpleName) {
        this(declaringScope, modifiers, simpleName);
        this.boundNode = node;
    }


    @Override
    public final JScope getDeclaringScope() {
        return declaringScope;
    }


    @Override
    public final Optional<N> getBoundNode() {
        return Optional.ofNullable(boundNode);
    }


    @Override
    public String getSimpleName() {
        return simpleName;
    }


    protected static int accessNodeToModifiers(AccessNode accessNode) {

        /*
        AccessNode:                     java.lang.reflect.Modifier

        int PUBLIC = 0x0001;            0x00000001;
        int PROTECTED = 0x0002;         0x00000004;
        int PRIVATE = 0x0004;           0x00000002
        int ABSTRACT = 0x0008;          0x00000400;
        int STATIC = 0x0010;            0x00000008;
        int FINAL = 0x0020;             0x00000010;
        int SYNCHRONIZED = 0x0040;      0x00000020;
        int NATIVE = 0x0080;            0x00000100;
        int TRANSIENT = 0x0100;         0x00000080;
        int VOLATILE = 0x0200;          0x00000040;
        int STRICTFP = 0x1000;          0x00000800;
        int DEFAULT = 0x2000;

        */

        int modifiers = accessNode.getModifiers();

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

}
