/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.coupling;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
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

    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (methodHasOverride(node)) {
            return data;
        }
        Node parent = node.getNthParent(3);
        String typeName = node.getImage();
        if (CollectionUtil.isCollectionType(typeName, false) && (parent instanceof ASTFieldDeclaration
                || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
            addViolation(data, node, typeName);
        }
        return data;
    }

    private boolean methodHasOverride(Node node) {
        ASTClassOrInterfaceBodyDeclaration method = node.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        if (method != null && method.jjtGetNumChildren() > 0 && method.jjtGetChild(0) instanceof ASTAnnotation) {
            ASTMarkerAnnotation marker = method.getFirstDescendantOfType(ASTMarkerAnnotation.class);
            if (marker != null && marker.getFirstChildOfType(ASTName.class) != null) {
                ASTName name = marker.getFirstChildOfType(ASTName.class);
                if ("Override".equals(name.getImage())) {
                    return true;
                }
            }
        }
        return false;
    }
}
