package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public interface ScopeCreationVisitor {
    void cont(SimpleNode node);
}
