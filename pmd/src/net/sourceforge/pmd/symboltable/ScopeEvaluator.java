/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public interface ScopeEvaluator {
    Scope getScopeFor(SimpleNode node);

    boolean isScopeCreatedBy(SimpleNode node);
}
