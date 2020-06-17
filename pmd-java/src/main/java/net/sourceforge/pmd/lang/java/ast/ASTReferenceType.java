/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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

    ASTReferenceType(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
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
