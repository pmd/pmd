/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.UnaryFunction;

import java.util.HashMap;
import java.util.Map;

public class VariableUsageFinderFunction implements UnaryFunction {

    private Map results = new HashMap();
    private Map decls;

    public VariableUsageFinderFunction(Map decls) {
        this.decls = decls;
    }

    public void applyTo(Object o) {
        results.put(o, decls.get(o));
    }

    public Map getUsed() {
        return results;
    }
}
