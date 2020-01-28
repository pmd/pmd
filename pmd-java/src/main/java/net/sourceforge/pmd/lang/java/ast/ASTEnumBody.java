/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Body of an {@linkplain ASTEnumDeclaration enum declaration}.
 *
 * <pre class="grammar">
 *
 * EnumBody ::= "{"
 *              [( {@link ASTAnnotation Annotation} )* {@link ASTEnumConstant EnumConstant} ( "," ( {@link ASTAnnotation Annotation} )* {@link ASTEnumConstant EnumConstant} )* ]
 *              [ "," ]
 *              [ ";" ( {@link ASTClassOrInterfaceBodyDeclaration ClassOrInterfaceBodyDeclaration} )* ]
 *              "}"
 *
 * </pre>
 *
 *
 */
public final class ASTEnumBody extends AbstractJavaNode implements ASTTypeBody {

    ASTEnumBody(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
