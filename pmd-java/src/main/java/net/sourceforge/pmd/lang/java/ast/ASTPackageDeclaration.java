/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Package declaration at the top of a {@linkplain ASTCompilationUnit source file}.
 * Since 7.0, there is no {@linkplain ASTName Name} node anymore. Use
 * {@link #getPackageNameImage()} instead.
 *
 *
 * <pre class="grammar">
 *
 * PackageDeclaration ::= "package" Name ";"
 *
 * </pre>
 *
 */
public final class ASTPackageDeclaration extends AbstractJavaAnnotatableNode {

    ASTPackageDeclaration(int id) {
        super(id);
    }

    ASTPackageDeclaration(JavaParser p, int id) {
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

    /**
     * Returns the name of the package.
     *
     * @since 4.2
     */
    // TODO @NoAttribute the Image.
    public String getPackageNameImage() {
        return getImage();
    }
}
