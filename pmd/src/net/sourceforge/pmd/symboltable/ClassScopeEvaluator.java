package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

public class ClassScopeEvaluator extends AbstractScopeEvaluator {
         public ClassScopeEvaluator() {
             triggers.add(ASTUnmodifiedClassDeclaration.class);
             triggers.add(ASTUnmodifiedInterfaceDeclaration.class);
         }
         public Scope getScopeFor(SimpleNode node) {
             return new ClassScope(node.getImage());
         }
     }

