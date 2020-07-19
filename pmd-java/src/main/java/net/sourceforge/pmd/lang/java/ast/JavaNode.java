/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;


/**
 * Root interface for all Nodes of the Java AST.
 */
public interface JavaNode extends JjtreeNode<JavaNode>, ScopedNode {

    /**
     * Calls back the visitor's visit method corresponding to the runtime type of this Node.
     *
     * @param visitor Visitor to dispatch
     * @param data    Visit data
     *
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return acceptVisitor(visitor, data);
    }


    @InternalApi
    @Deprecated
    void setScope(Scope scope);

    @Override
    @NonNull ASTCompilationUnit getRoot();

}
