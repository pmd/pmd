/*
 * User: tom
 * Date: Oct 29, 2002
 * Time: 10:50:24 AM
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
        NameDeclaration nameDeclaration = (NameDeclaration)o;
        List usages = (List)decls.get(nameDeclaration);
        if (!usages.isEmpty()) {
            if (lookingForUsed) {
                results.put(nameDeclaration, usages);
            }
        } else {
            if (!lookingForUsed) {
                results.put(nameDeclaration, usages);
            }
        }
    }

    public Map getUsed() {
        return results;
    }
}
