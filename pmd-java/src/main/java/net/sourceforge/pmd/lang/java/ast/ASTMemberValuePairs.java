/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents a list of member values in an {@linkplain ASTNormalAnnotation annotation}.
 *
 * <pre>
 *
 *  MemberValuePairs ::= {@linkplain ASTMemberValuePair MemberValuePair} ( "," {@linkplain ASTMemberValuePair MemberValuePair} )*
 *
 * </pre>
 */
public class ASTMemberValuePairs extends AbstractJavaNode implements Iterable<ASTMemberValuePair> {

    @InternalApi
    @Deprecated
    public ASTMemberValuePairs(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTMemberValuePairs(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public ASTMemberValuePair getChild(int index) {
        return (ASTMemberValuePair) super.getChild(index);
    }


    @Override
    public ASTNormalAnnotation getParent() {
        return (ASTNormalAnnotation) super.getParent();
    }


    @Override
    public Iterator<ASTMemberValuePair> iterator() {
        return new NodeChildrenIterator<>(this, ASTMemberValuePair.class);
    }
}
