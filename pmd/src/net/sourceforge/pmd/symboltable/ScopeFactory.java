package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Stack;

public interface ScopeFactory {
    void openScope(Stack scopes, SimpleNode node);
}
