/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

public class ClassScopeEvaluator extends AbstractScopeEvaluator {
         public ClassScopeEvaluator() {
             triggers.add(ASTUnmodifiedClassDeclaration.class);
             triggers.add(ASTUnmodifiedInterfaceDeclaration.class);
             triggers.add(ASTClassBodyDeclaration.class);
         }
         public Scope getScopeFor(SimpleNode node) {
             if (node instanceof ASTClassBodyDeclaration) {
                 return new ClassScope();
             }
             return new ClassScope(node.getImage());
         }
     }

