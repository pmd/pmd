/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Represents a list of type parameters.
 *
 * <pre class="grammar">
 *
 * TypeParameters ::= "<" {@linkplain ASTTypeParameter TypeParameter} ( "," {@linkplain ASTTypeParameter TypeParameter} )* ">"
 *
 * </pre>
 */
public final class ASTTypeParameters extends AbstractJavaNode implements Iterable<ASTTypeParameter> {

    ASTTypeParameters(int id) {
        super(id);
    }

    ASTTypeParameters(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    // quite sloppy but safe at runtime
    @SuppressWarnings("unchecked")
    public List<ASTTypeParameter> asList() {
        return (List<ASTTypeParameter>) (Object) Arrays.asList(children);
    }


    @Override
    public Iterator<ASTTypeParameter> iterator() {
        return children(ASTTypeParameter.class).iterator();
    }
}
