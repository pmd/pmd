/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 2:39:34 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.Set;
import java.util.HashSet;

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
            return new ClassScope(((SimpleNode)node).getImage());
        } else if (globalTriggers.contains(node.getClass())) {
            return new GlobalScope();
        }
        throw new RuntimeException("Can't find an appropriate scope for node of class " + node.getClass());
    }

    private void initScopeTriggers() {
        localTriggers.add(ASTBlock.class);
        localTriggers.add(ASTTryStatement.class);
        localTriggers.add(ASTForStatement.class);
        localTriggers.add(ASTIfStatement.class);
        methodTriggers.add(ASTConstructorDeclaration.class);
        methodTriggers.add(ASTMethodDeclaration.class);
        classTriggers.add(ASTUnmodifiedClassDeclaration.class);
        classTriggers.add(ASTUnmodifiedInterfaceDeclaration.class);
        globalTriggers.add(ASTCompilationUnit.class);
    }
}
