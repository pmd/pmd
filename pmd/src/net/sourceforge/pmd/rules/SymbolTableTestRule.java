/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map methods = node.getScope().getEnclosingClassScope().getMethodDeclarations();
        Set suffixes = new HashSet();
        for (Iterator i = methods.keySet().iterator(); i.hasNext();) {
            MethodNameDeclaration mnd = (MethodNameDeclaration) i.next();
            String suffix = findSuffix(mnd);
            if (suffix != null) {
                if (suffixes.contains(suffix)) {
                    addViolation(data, mnd.getNode(), suffix);
                }
                suffixes.add(suffix);
            }
        }
        return data;
    }

    private String findSuffix(MethodNameDeclaration mnd) {
        String end = null;
        if (mnd.getImage().startsWith("is")) {
            end = mnd.getImage().substring(2);
        } else if (mnd.getImage().startsWith("get")) {
            end = mnd.getImage().substring(3);
        }
        return end;
    }

}
