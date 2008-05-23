/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.util.UnaryFunction;

public class VariableUsageFinderFunction implements UnaryFunction<VariableNameDeclaration> {
    
    private Map<VariableNameDeclaration, List<NameOccurrence>> results = new HashMap<VariableNameDeclaration, List<NameOccurrence>>();

    private Map<VariableNameDeclaration, List<NameOccurrence>> decls;

    public VariableUsageFinderFunction(Map<VariableNameDeclaration, List<NameOccurrence>> decls) {
        this.decls = decls;
    }

    public void applyTo(VariableNameDeclaration o) {
        results.put(o, decls.get(o));
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getUsed() {
        return results;
    }
}
