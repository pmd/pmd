/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:13:59 AM
 */
package net.sourceforge.pmd.symboltable;

public class AbstractScope {

    private Scope parent;

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public Scope getParent() {
        return parent;
    }
}
