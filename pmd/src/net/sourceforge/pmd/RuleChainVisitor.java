package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.ast.CompilationUnit;

/**
 * The RuleChainVisitor understands how to visit an AST for a particular
 * Language.
 */
public interface RuleChainVisitor {
    /**
     * Add the given rule to the visitor.
     * 
     * @param rule
     *            The rule to add.
     */
    void add(Rule rule);

    /**
     * Visit all the given ASTCompilationUnits provided using the given
     * RuleContext. Every Rule added will visit the AST as appropriate.
     * 
     * @param astCompilationUnits
     *            The ASTCompilationUnits to visit.
     * @param ctx
     *            The RuleContext.
     */
    void visitAll(List<CompilationUnit> astCompilationUnits, RuleContext ctx);
}
