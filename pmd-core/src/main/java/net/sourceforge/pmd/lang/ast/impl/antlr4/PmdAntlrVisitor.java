/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 * Interface for parse tree visitors that can also visit PMD nodes. The
 * antlr rewrite makes the generated visitor interface extend this.
 */
public interface PmdAntlrVisitor<T> extends ParseTreeVisitor<T> {

    @Override
    T visitChildren(RuleNode node);


    /**
     * This is added for compatibility.
     */
    default T visitChildren(BaseAntlrInnerNode<?> node) {
        return visitChildren(node.asAntlrNode());
    }

}
