/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents the {@code implements} clause of a class declaration.
 *
 * <pre>
 *  ExtendsList ::= "implements" (TypeAnnotation)* ClassOrInterfaceType
 *                ( "," (TypeAnnotation)* ClassOrInterfaceType )*
 * </pre>
 */
public class ASTImplementsList extends AbstractJavaNode implements Iterable<ASTClassOrInterfaceType> {

    @InternalApi
    @Deprecated
    public ASTImplementsList(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTImplementsList(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public Iterator<ASTClassOrInterfaceType> iterator() {
        return new NodeChildrenIterator<>(this, ASTClassOrInterfaceType.class);
    }
}
