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
        Scope result = new NullScope();
        if (localTriggers.contains(node.getClass())) {
            result = new LocalScope();
        } else if (functionTriggers.contains(node.getClass())) {
            result = new FunctionScope();
        } else if (classTriggers.contains(node.getClass())) {
            if (node instanceof ASTUnmodifiedClassDeclaration) {
                result = new ClassScope(((ASTUnmodifiedClassDeclaration)node).getClassName());
            } else {
                result = new ClassScope("UNKNOWN");
            }
        } else if (globalTriggers.contains(node.getClass())) {
            result = new GlobalScope();
        }
        return result;
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
