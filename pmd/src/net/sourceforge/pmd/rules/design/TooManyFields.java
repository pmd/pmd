/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class TooManyFields extends AbstractRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private Map stats;
    private Map nodes;
    private int maxFields;

    public Object visit(ASTCompilationUnit node, Object data) {
        maxFields = hasProperty("maxfields") ? getIntProperty("maxfields") : DEFAULT_MAXFIELDS;

        stats = new HashMap(5);
        nodes = new HashMap(5);

        List l = node.findChildrenOfType(ASTFieldDeclaration.class);

        for (Iterator it = l.iterator(); it.hasNext();) {
            ASTFieldDeclaration fd = (ASTFieldDeclaration) it.next();
            if (fd.isFinal() && fd.isStatic()) {
                continue;
            }
            ASTClassOrInterfaceDeclaration clazz = (ASTClassOrInterfaceDeclaration) fd.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
            if (!clazz.isInterface()) {
                bumpCounterFor(clazz);
            }
        }
        for (Iterator it = stats.keySet().iterator(); it.hasNext();) {
            String k = (String) it.next();
            int val = ((Integer) stats.get(k)).intValue();
            SimpleNode n = (SimpleNode) nodes.get(k);
            if (val > maxFields) {
                addViolation(data, n);
            }
        }
        return data;
    }

    private void bumpCounterFor(ASTClassOrInterfaceDeclaration clazz) {
        String key = clazz.getImage();
        if (!stats.containsKey(key)) {
            stats.put(key, new Integer(0));
            nodes.put(key, clazz);
        }
        Integer i = new Integer(((Integer) stats.get(key)).intValue() + 1);
        stats.put(key, i);
    }

}
