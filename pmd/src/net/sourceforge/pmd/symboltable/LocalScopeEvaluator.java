/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.SimpleNode;

public class LocalScopeEvaluator extends AbstractScopeEvaluator {
         public LocalScopeEvaluator() {
             triggers.add(ASTBlock.class);
             triggers.add(ASTTryStatement.class);
             triggers.add(ASTForStatement.class);
             triggers.add(ASTSwitchStatement.class);
             triggers.add(ASTIfStatement.class);
         }
         public Scope getScopeFor(SimpleNode node) {
             return new LocalScope();
         }
     }

