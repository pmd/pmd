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
    private Set functionTriggers = new HashSet();
    private Set classTriggers = new HashSet();
    private Set globalTriggers = new HashSet();

    public ScopeFactory() {
        initScopeTriggers();
    }

    public Scope createScope(Node node) {
        if (localTriggers.contains(node.getClass())) {
            return new LocalScope();
        } else if (functionTriggers.contains(node.getClass())) {
            return new MethodScope();
        } else if (classTriggers.contains(node.getClass())) {
            if (node instanceof ASTUnmodifiedClassDeclaration) {
                return new ClassScope(((ASTUnmodifiedClassDeclaration)node).getClassName());
            } else {
                return new ClassScope("UNKNOWN");
            }
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
        functionTriggers.add(ASTConstructorDeclaration.class);
        functionTriggers.add(ASTMethodDeclaration.class);
        classTriggers.add(ASTUnmodifiedClassDeclaration.class);
        globalTriggers.add(ASTCompilationUnit.class);
    }
}
