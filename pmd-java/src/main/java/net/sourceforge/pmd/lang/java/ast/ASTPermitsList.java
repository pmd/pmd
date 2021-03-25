/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.Experimental;


/**
 * Represents the {@code permits} clause of a (sealed) class declaration.
 *
 * <p>This is a Java 15 Preview and Java 16 Preview feature.
 *
 * <p>See https://openjdk.java.net/jeps/397
 *
 * <pre class="grammar">
 *
 *  PermitsList ::= "permits" ClassOrInterfaceType
 *                ( "," ClassOrInterfaceType )*
 * </pre>
 */
@Experimental
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
