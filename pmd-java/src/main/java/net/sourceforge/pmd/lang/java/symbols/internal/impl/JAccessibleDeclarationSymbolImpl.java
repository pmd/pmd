/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.symbols.internal.JAccessibleDeclarationSymbol;


public abstract class JAccessibleDeclarationSymbolImpl<N extends Node>
    extends AbstractDeclarationSymbol<N>
    implements JAccessibleDeclarationSymbol<N> {

    final int myModifiers;
    private final JClassSymbolImpl myEnclosingClass;


    JAccessibleDeclarationSymbolImpl(int modifiers, String simpleName, Class<?> enclosingClass) {
        super(simpleName);
        this.myModifiers = modifiers;
        this.myEnclosingClass = enclosingClass == null ? null
                                                       : JClassSymbolImpl.create(enclosingClass);
    }


    JAccessibleDeclarationSymbolImpl(N node, int modifiers, String simpleName) {
        super(node, simpleName);
        this.myModifiers = modifiers;
        this.myEnclosingClass =
            // doesn't handle anonymous type declaration until we handle #905
            Optional.ofNullable(node.getFirstParentOfType(ASTAnyTypeDeclaration.class))
                    .map(JClassSymbolImpl::create)
                    .orElse(null);
    }


    @Override
    public JClassSymbolImpl getEnclosingClass() {
        return myEnclosingClass;
    }


    @Override
    public final boolean isPublic() {
        return Modifier.isPublic(myModifiers);
    }


    @Override
    public final boolean isPrivate() {
        return Modifier.isPrivate(myModifiers);
    }


    @Override
    public final boolean isProtected() {
        return Modifier.isProtected(myModifiers);
    }


    @Override
    public final boolean isPackagePrivate() {
        return (myModifiers & (Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC)) == 0;
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
        JAccessibleDeclarationSymbolImpl<?> that = (JAccessibleDeclarationSymbolImpl<?>) o;
        return myModifiers == that.myModifiers && myEnclosingClass.equals(that.myEnclosingClass);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), myModifiers);
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


}
