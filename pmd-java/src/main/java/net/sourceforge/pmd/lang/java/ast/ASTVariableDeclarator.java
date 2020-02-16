/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Groups a variable ID and its initializer if it exists.
 * May be found as a child of {@linkplain ASTFieldDeclaration field declarations} and
 * {@linkplain ASTLocalVariableDeclaration local variable declarations}.
 *
 * <pre>
 *
 * VariableDeclarator ::= {@linkplain ASTVariableDeclaratorId VariableDeclaratorId} ( "=" {@linkplain ASTVariableInitializer VariableInitializer} )?
 *
 * </pre>
 */
public class ASTVariableDeclarator extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTVariableDeclarator(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTVariableDeclarator(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the name of the declared variable.
     */
    public String getName() {
        // first child will be VariableDeclaratorId
        return getChild(0).getImage();
    }


    /**
     * Returns the id of the declared variable.
     */
    public ASTVariableDeclaratorId getVariableId() {
        return (ASTVariableDeclaratorId) getChild(0);
    }


    /**
     * Returns true if the declared variable is initialized.
     * Otherwise, {@link #getInitializer()} returns null.
     */
    public boolean hasInitializer() {
        return getNumChildren() > 1;
    }


    /**
     * Returns the initializer, of the variable, or null if it doesn't exist.
     */
    public ASTVariableInitializer getInitializer() {
        return hasInitializer() ? (ASTVariableInitializer) getChild(1) : null;
    }


    /* only for LocalVarDeclaration and FieldDeclaration */
    static Iterator<ASTVariableDeclaratorId> iterateIds(Node parent) {
        // TODO this can be made clearer with iterator mapping (Java 8)
        final Iterator<ASTVariableDeclarator> declarators = new NodeChildrenIterator<>(parent, ASTVariableDeclarator.class);

        return new Iterator<ASTVariableDeclaratorId>() {
            @Override
            public boolean hasNext() {
                return declarators.hasNext();
            }


            @Override
            public ASTVariableDeclaratorId next() {
                return declarators.next().getVariableId();
            }


            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
