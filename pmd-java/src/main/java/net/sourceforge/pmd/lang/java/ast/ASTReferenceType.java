/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a reference type, i.e. a {@linkplain ASTClassOrInterfaceType class or interface type},
 * or an array type.
 *
 * <pre>
 *
 *  ReferenceType ::= {@linkplain ASTPrimitiveType PrimitiveType} {@linkplain ASTAnnotation Annotation}* ( "[" "]" )+
 *                  | {@linkplain ASTClassOrInterfaceType ClassOrInterfaceType} {@linkplain ASTAnnotation Annotation}* ( "[" "]" )*
 *
 * </pre>
 */
public class ASTReferenceType extends AbstractJavaTypeNode implements Dimensionable {

    private int arrayDepth;

    @InternalApi
    @Deprecated
    public ASTReferenceType(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTReferenceType(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Deprecated
    public void bumpArrayDepth() {
        arrayDepth++;
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        return arrayDepth;
    }

    @Override
    @Deprecated
    public boolean isArray() {
        return arrayDepth > 0;
    }

}
