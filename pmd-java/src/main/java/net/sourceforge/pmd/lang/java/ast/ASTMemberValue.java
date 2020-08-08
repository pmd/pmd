/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents the value of a member of an annotation.
 * This can appear in a {@linkplain ASTMemberValuePair member-value pair},
 * or in a {@linkplain ASTSingleMemberAnnotation single-member annotation}.
 *
 * <pre>
 *
 * MemberValue ::= {@linkplain ASTAnnotation Annotation}
 *               | {@linkplain ASTMemberValueArrayInitializer MemberValueArrayInitializer}
 *               | &lt; any expression, excluding assignment expressions and lambda expressions &gt;
 *
 * </pre>
 */
public class ASTMemberValue extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTMemberValue(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
