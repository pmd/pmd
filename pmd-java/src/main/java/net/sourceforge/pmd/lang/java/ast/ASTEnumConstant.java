/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


/**
 * Represents an enum constant declaration within an {@linkplain ASTEnumDeclaration enum declaration}.
 *
 * <p>TODO since there's no VariableDeclaratorId, this might not play well with the symbol table!
 *
 * <pre>
 *
 * EnumConstant ::= &lt;IDENTIFIER&gt; {@linkplain ASTArguments Arguments}? {@linkplain ASTClassOrInterfaceBody ClassOrInterfaceBody}?
 *
 * </pre>
 */
public class ASTEnumConstant extends AbstractJavaNode implements JavaQualifiableNode {

    private JavaTypeQualifiedName qualifiedName;

    @InternalApi
    @Deprecated
    public ASTEnumConstant(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTEnumConstant(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Gets the qualified name of the anonymous class
     * declared by this node, or null if this node
     * doesn't declare any.
     *
     * @see #isAnonymousClass()
     */
    @Override
    public JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }


    @InternalApi
    @Deprecated
    public void setQualifiedName(JavaTypeQualifiedName qname) {
        this.qualifiedName = qname;
    }


    /**
     * Returns true if this enum constant defines a body,
     * which is compiled like an anonymous class. If this
     * method returns false, then {@link #getQualifiedName()}
     * returns {@code null}.
     */
    public boolean isAnonymousClass() {
        return getFirstChildOfType(ASTClassOrInterfaceBody.class) != null;
    }

}
