/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents the {@code extends} clause of a class or interface declaration.
 * If the parent is an interface declaration, then these types are all interface
 * types. Otherwise, then this list contains exactly one element.
 *
 * <pre>
 *  ExtendsList ::= "extends" (TypeAnnotation)* ClassOrInterfaceType
 *                ( "," (TypeAnnotation)* ClassOrInterfaceType )*
 * </pre>
 */
public class ASTExtendsList extends AbstractJavaNode implements Iterable<ASTClassOrInterfaceType> {

    @InternalApi
    @Deprecated
    public ASTExtendsList(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override    // TODO this doesn't preserve the annotations.
    public Iterator<ASTClassOrInterfaceType> iterator() {
        return children(ASTClassOrInterfaceType.class).iterator();
    }
}
