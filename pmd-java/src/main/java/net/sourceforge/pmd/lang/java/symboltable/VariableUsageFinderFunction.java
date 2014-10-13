/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.UnaryFunction;

public class VariableUsageFinderFunction implements UnaryFunction<NameDeclaration> {
    
    private Map<NameDeclaration, List<NameOccurrence>> results = new HashMap<NameDeclaration, List<NameOccurrence>>();

    private Map<NameDeclaration, List<NameOccurrence>> decls;

    public VariableUsageFinderFunction(Map<NameDeclaration, List<NameOccurrence>> decls) {
        this.decls = decls;
    }

    public void applyTo(NameDeclaration o) {
        results.put(o, decls.get(o));
    }

    public Map<NameDeclaration, List<NameOccurrence>> getUsed() {
        return results;
    }
}
