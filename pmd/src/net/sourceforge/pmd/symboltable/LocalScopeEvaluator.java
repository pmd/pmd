/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

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

