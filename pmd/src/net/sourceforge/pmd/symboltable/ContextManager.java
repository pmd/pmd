/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:38 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

public interface ContextManager {
    public Scope getCurrentScope();
    public void openScope(Scope scope);
    public void leaveScope();
}
