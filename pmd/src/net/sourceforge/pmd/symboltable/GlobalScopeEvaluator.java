/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;

public class GlobalScopeEvaluator extends AbstractScopeEvaluator {
         public GlobalScopeEvaluator() {
             triggers.add(ASTCompilationUnit.class);
         }
         public Scope getScopeFor(SimpleNode node) {
             return new GlobalScope();
         }
     }

