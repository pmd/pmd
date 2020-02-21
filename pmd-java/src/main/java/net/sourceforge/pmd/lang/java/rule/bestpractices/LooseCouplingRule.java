/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.CollectionUtil;

public class LooseCouplingRule extends AbstractJavaRule {

    // TODO - these should be brought in via external properties
    // private static final Set implClassNames = CollectionUtil.asSet( new
    // Object[] {
    // "ArrayList", "HashSet", "HashMap", "LinkedHashMap", "LinkedHashSet",
    // "TreeSet", "TreeMap", "Vector",
    // "java.util.ArrayList", "java.util.HashSet", "java.util.HashMap",
    // "java.util.LinkedHashMap", "java.util.LinkedHashSet",
    // "java.util.TreeSet",
    // "java.util.TreeMap", "java.util.Vector"
    // });

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (methodHasOverride(node)) {
            return data;
        }
        Node parent = node.getNthParent(3);
        Class<?> clazzType = node.getType();
        boolean isType = CollectionUtil.isCollectionType(clazzType, false);
        if (isType && (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter
                || parent instanceof ASTResultType)) {
            addViolation(data, node, node.getImage());
        }
        return data;
    }

    private boolean methodHasOverride(JavaNode node) {
        ASTMethodDeclaration method = node.ancestors(ASTMethodDeclaration.class).first();
        return method != null && method.isAnnotationPresent(Override.class);
    }
}
