package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public interface ScopeFactory {
    void openScope(ScopeCreationVisitor vis, SimpleNode node);
    Scope getCurrentScope();
}
