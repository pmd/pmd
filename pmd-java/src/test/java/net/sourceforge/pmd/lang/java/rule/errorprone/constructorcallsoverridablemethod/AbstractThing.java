/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.constructorcallsoverridablemethod;

public abstract class AbstractThing implements Thing {
    protected AbstractThing(Thing original) {
        setName(original.getName());
    }

    @Override
    public void setName(String name) { }

    @Override
    public String getName() {
        return "";
    }
}
