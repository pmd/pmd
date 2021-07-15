/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents the {@code permits} clause of a (sealed) class declaration.
 *
 * <p>This is a Java 17 Feature.
 *
 * <p>See https://openjdk.java.net/jeps/409
 *
 * <pre class="grammar">
 *
 *  PermitsList ::= "permits" ClassOrInterfaceType
 *                ( "," ClassOrInterfaceType )*
 * </pre>
 */
public final class ASTPermitsList extends AbstractJavaNode implements Iterable<ASTClassOrInterfaceType> {

    ASTPermitsList(int id) {
        super(id);
    }

    ASTPermitsList(JavaParser p, int id) {
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
