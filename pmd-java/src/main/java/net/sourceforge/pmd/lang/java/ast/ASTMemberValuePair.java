/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a single member-value pair in an annotation.
 *
 * <pre>
 *
 * MemberValuePair ::=  &lt;IDENTIFIER&gt; "=" {@linkplain ASTMemberValue MemberValue}
 *
 * </pre>
 */
public class ASTMemberValuePair extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTMemberValuePair(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTMemberValuePair(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the name of the member set by this pair.
     */
    public String getMemberName() {
        return getImage();
    }


    /**
     * Returns the value of the member set by this pair.
     */
    public ASTMemberValue getMemberValue() {
        return (ASTMemberValue) getChild(0);
    }


    @Override
    public ASTMemberValuePairs getParent() {
        return (ASTMemberValuePairs) super.getParent();
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
