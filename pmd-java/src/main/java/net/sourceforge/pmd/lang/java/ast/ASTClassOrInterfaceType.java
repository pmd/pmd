/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Represents a class or interface type, possibly parameterised with type arguments.
 *
 * <pre>
 *
 * ClassOrInterfaceType ::= &lt;IDENTIFIER&gt; {@linkplain ASTTypeArguments TypeArguments}? ( "." &lt;IDENTIFIER&gt;  {@linkplain ASTTypeArguments TypeArguments}? )*
 *
 * </pre>
 */
public class ASTClassOrInterfaceType extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTClassOrInterfaceType(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTClassOrInterfaceType(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Checks whether the type this node is referring to is declared within the
     * same compilation unit - either a class/interface or a enum type. You want
     * to check this, if {@link #getType()} is null.
     *
     * @return <code>true</code> if this node referencing a type in the same
     *     compilation unit, <code>false</code> otherwise.
     */
    public boolean isReferenceToClassSameCompilationUnit() {
        ASTCompilationUnit root = getFirstParentOfType(ASTCompilationUnit.class);
        for (ASTClassOrInterfaceDeclaration c : root.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class, true)) {
            if (c.hasImageEqualTo(getImage())) {
                return true;
            }
        }
        for (ASTEnumDeclaration e : root.findDescendantsOfType(ASTEnumDeclaration.class, true)) {
            if (e.hasImageEqualTo(getImage())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnonymousClass() {
        return getParent().getFirstChildOfType(ASTClassOrInterfaceBody.class) != null;
    }

    public boolean isArray() {
        Node p = getParent();
        if (p instanceof ASTReferenceType) {
            return ((ASTReferenceType) p).isArray();
        }
        return false;
    }

    public int getArrayDepth() {
        Node p = getParent();
        if (p instanceof ASTReferenceType) {
            return ((ASTReferenceType) p).getArrayDepth();
        }
        return 0;
    }
}
