/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

public class MethodScopeEvaluator extends AbstractScopeEvaluator {
         public MethodScopeEvaluator() {
             triggers.add(ASTConstructorDeclaration.class);
             triggers.add(ASTMethodDeclaration.class);
         }
         public Scope getScopeFor(SimpleNode node) {
             return new MethodScope();
         }
     }

