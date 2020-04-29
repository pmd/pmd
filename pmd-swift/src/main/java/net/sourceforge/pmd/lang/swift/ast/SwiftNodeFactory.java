/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.antlr.v4.runtime.tree.RuleNode;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrParseTreeBase;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser.TopLevelContext;


final class SwiftNodeFactory extends SwiftTreeBaseVisitor<SwiftNodeImpl<?>> {

    static final SwiftNodeFactory INSTANCE = new SwiftNodeFactory();

    private SwiftNodeFactory() {

    }

    @Override
    public SwiftNodeImpl<?> visitTopLevel(TopLevelContext ctx) {
        return new SwiftRootNode(ctx);
    }

    @Override
    public SwiftNodeImpl<?> visitChildren(RuleNode node) {
        // default visit
        return new SwiftNodeImpl<>((AntlrParseTreeBase) node);
    }
}
