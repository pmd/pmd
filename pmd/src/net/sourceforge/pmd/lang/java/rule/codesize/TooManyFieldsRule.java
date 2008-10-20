/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.util.NumericConstants;


public class TooManyFieldsRule extends AbstractJavaRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private Map<String, Integer> stats;
    private Map<String, ASTClassOrInterfaceDeclaration> nodes;

    private static final IntegerProperty MAX_FIELDS_DESCRIPTOR = new IntegerProperty(
    		"maxfields", "Max allowable fields",
    		1, 300, DEFAULT_MAXFIELDS, 1.0f
    		);
    
    public TooManyFieldsRule() {
	definePropertyDescriptor(MAX_FIELDS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {

        int maxFields = getProperty(MAX_FIELDS_DESCRIPTOR);

        stats = new HashMap<String, Integer>(5);
        nodes = new HashMap<String, ASTClassOrInterfaceDeclaration>(5);

        List<ASTFieldDeclaration> l = node.findDescendantsOfType(ASTFieldDeclaration.class);

        for (ASTFieldDeclaration fd: l) {
            if (fd.isFinal() && fd.isStatic()) {
                continue;
            }
            ASTClassOrInterfaceDeclaration clazz = fd.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
            if (clazz != null && !clazz.isInterface()) {
                bumpCounterFor(clazz);
            }
        }
        for (String k : stats.keySet()) {
            int val = stats.get(k);
            Node n = nodes.get(k);
            if (val > maxFields) {
                addViolation(data, n);
            }
        }
        return data;
    }

    private void bumpCounterFor(ASTClassOrInterfaceDeclaration clazz) {
        String key = clazz.getImage();
        if (!stats.containsKey(key)) {
            stats.put(key, NumericConstants.ZERO);
            nodes.put(key, clazz);
        }
        Integer i = Integer.valueOf(stats.get(key) + 1);
        stats.put(key, i);
    }
}
