/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AbstractAntlrNode;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.IdentifierContext;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.TopLevelContext;


final class SwiftNodeFactory extends SwiftTreeBaseVisitor<AbstractAntlrNode<?, SwiftNode<?>>> {

    static final SwiftNodeFactory INSTANCE = new SwiftNodeFactory();

    private SwiftNodeFactory() {

    }

    @Override
    public SwiftTerminal visitTerminal(TerminalNode node) {
        return new SwiftTerminal(node);
    }

    @Override
    public SwiftInnerNode<?> visitIdentifier(IdentifierContext ctx) {
        return new SwIdentifier(ctx);
    }

    @Override
    public SwiftInnerNode<?> visitTopLevel(TopLevelContext ctx) {
        return new SwRootNode(ctx);
    }

    @Override
    public SwiftInnerNode<?> visitChildren(RuleNode node) {
        // default visit
        return new SwiftInnerNode<>((AntlrParseTreeBase) node);
    }
}
