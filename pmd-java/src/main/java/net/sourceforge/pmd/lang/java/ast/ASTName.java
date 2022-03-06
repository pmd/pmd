/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A (possibly qualified) name. Note: since PMD 7, this node is only
 * found in module declarations.
 *
 * TODO(#2701): revisit module declarations
 */
public class ASTName extends AbstractJavaNode {

    ASTName(int id) {
        super(id);
    }



    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
