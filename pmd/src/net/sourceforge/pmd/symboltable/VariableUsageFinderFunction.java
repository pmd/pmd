/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.UnaryFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariableUsageFinderFunction implements UnaryFunction {
    private Map results = new HashMap();
    private Map decls;
    private boolean lookingForUsed;

    public VariableUsageFinderFunction(Map decls, boolean lookingForUsed) {
        this.decls = decls;
        this.lookingForUsed = lookingForUsed;
    }

    public void applyTo(Object o) {
        NameDeclaration decl = (NameDeclaration) o;
        List usages = (List) decls.get(decl);
        if (!usages.isEmpty()) {
            if (lookingForUsed) {
                results.put(decl, usages);
            }
        } else {
            if (!lookingForUsed) {
                results.put(decl, usages);
            }
        }
    }

    public Map getUsed() {
        return results;
    }
}
