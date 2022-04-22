/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The name of a module. Module names look like package names, eg
 * {@code java.base}.
 */
public final class ASTModuleName extends AbstractJavaNode {

    ASTModuleName(int id) {
        super(id);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    @Deprecated
    public String getImage() {
        return null;
    }

    /**
     * Returns the name of the declared module. Module names look
     * like package names, eg {@code java.base}.
     */
    public String getName() {
        return super.getImage();
    }

}
