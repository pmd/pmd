/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A module declaration. This is found at the top-level of a
 * {@linkplain ASTCompilationUnit modular compilation unit}.
 *
 * <pre clas="grammar">
 *
 * ModuleDeclaration ::= {@linkplain ASTModifierList AnnotationList} "open"?
 *                       "module" {@linkplain ASTModuleName ModuleName}
 *                       "{" {@linkplain ASTModuleDirective ModuleDirective}* "}"
 *
 * </pre>
 */
public final class ASTModuleDeclaration extends AbstractJavaNode implements Annotatable {

    private boolean open;

    ASTModuleDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the name of the declared module. Module names look
     * like package names, eg {@code java.base}.
     */
    public String getName() {
        return firstChild(ASTModuleName.class).getName();
    }

    /**
     * Returns a stream with all directives declared by the module.
     */
    public NodeStream<ASTModuleDirective> getDirectives() {
        return children(ASTModuleDirective.class);
    }

    void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
