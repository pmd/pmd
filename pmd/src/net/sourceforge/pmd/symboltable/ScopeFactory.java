package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedInterfaceDeclaration;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashSet;
import java.util.Set;

public class ScopeFactory {

    private Set localTriggers = new HashSet();
    private Set methodTriggers = new HashSet();
    private Set classTriggers = new HashSet();
    private Set globalTriggers = new HashSet();

    public ScopeFactory() {
        initScopeTriggers();
    }

    public Scope createScope(Node node) {
        if (localTriggers.contains(node.getClass())) {
            return new LocalScope();
        } else if (methodTriggers.contains(node.getClass())) {
            return new MethodScope();
        } else if (classTriggers.contains(node.getClass())) {
            return new ClassScope(((SimpleNode) node).getImage());
        } else if (globalTriggers.contains(node.getClass())) {
            return new GlobalScope();
        }
        throw new RuntimeException("Can't find an appropriate scope for node of class " + node.getClass());
    }

    private void initScopeTriggers() {
        localTriggers.add(ASTBlock.class);
        localTriggers.add(ASTTryStatement.class);
        localTriggers.add(ASTForStatement.class);
        localTriggers.add(ASTSwitchStatement.class);
        localTriggers.add(ASTIfStatement.class);
        methodTriggers.add(ASTConstructorDeclaration.class);
        methodTriggers.add(ASTMethodDeclaration.class);
        classTriggers.add(ASTUnmodifiedClassDeclaration.class);
        classTriggers.add(ASTUnmodifiedInterfaceDeclaration.class);
        globalTriggers.add(ASTCompilationUnit.class);
    }
}
