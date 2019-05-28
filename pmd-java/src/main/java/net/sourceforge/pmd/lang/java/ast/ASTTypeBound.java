/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents a type bound on a {@linkplain ASTTypeParameter type parameter}.
 * Type bounds specify the type of the type variable to which they apply as
 * an <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-4.html#jls-4.9">intersection type</a>.
 * The first bound type is a class or interface type, while the additional
 * bounds are necessarily interface types.
 *
 * <pre>
 *
 * TypeBound ::= "extends" {@linkplain ASTAnnotation Annotation}* {@linkplain ASTClassOrInterfaceType ClassOrInterfaceType} ( "&" {@linkplain ASTAnnotation Annotation}* {@linkplain ASTClassOrInterfaceType ClassOrInterfaceType} )*
 *
 * </pre>
 */
public class ASTTypeBound extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTTypeBound(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTTypeBound(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns a list with the type bounds of this node.
     * The returned list has at least one element.
     */
    public List<ASTClassOrInterfaceType> getBoundTypeNodes() {
        return findChildrenOfType(ASTClassOrInterfaceType.class);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
