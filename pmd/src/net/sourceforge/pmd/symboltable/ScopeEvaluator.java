/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public interface ScopeEvaluator {
    public Scope getScopeFor(SimpleNode node);
    public boolean isScopeCreatedBy(SimpleNode node);
}
