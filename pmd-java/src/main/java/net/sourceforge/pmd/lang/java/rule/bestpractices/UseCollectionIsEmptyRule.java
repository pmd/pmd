/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Detect structures like "foo.size() == 0" and suggest replacing them with
 * foo.isEmpty(). Will also find != 0 (replaceable with !isEmpty()).
 *
 * @author Jason Bennett
 */
public class UseCollectionIsEmptyRule extends AbstractInefficientZeroCheck {

    @Override
    public boolean appliesToClassName(String name) {
        return CollectionUtil.isCollectionType(name, true);
    }

    /**
     * Determine if we're dealing with .size method
     *
     * @param occ
     *            The name occurrence
     * @return true if it's .size, else false
     */
    @Override
    public boolean isTargetMethod(JavaNameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null) {
            if (occ.getLocation().getImage().endsWith(".size")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, List<String>> getComparisonTargets() {
        Map<String, List<String>> rules = new HashMap<>();
        rules.put("<", Arrays.asList("0", "1"));
        rules.put(">", Arrays.asList("0"));
        rules.put("==", Arrays.asList("0"));
        rules.put("!=", Arrays.asList("0"));
        rules.put(">=", Arrays.asList("0", "1"));
        rules.put("<=", Arrays.asList("0"));
        return rules;
    }

    @Override
    public Object visit(ASTPrimarySuffix node, Object data) {
        if (node.getImage() != null && node.getImage().endsWith("size")) {

            ASTClassOrInterfaceType type = getTypeOfPrimaryPrefix(node);
            if (type == null) {
                type = getTypeOfMethodCall(node);
            }

            if (type != null && CollectionUtil.isCollectionType(type.getType(), true)) {
                Node expr = node.getParent().getParent();
                checkNodeAndReport(data, node, expr);
            }
        }
        return data;
    }

    private ASTClassOrInterfaceType getTypeOfMethodCall(ASTPrimarySuffix node) {
        ASTClassOrInterfaceType type = null;
        ASTName methodName = node.getParent().getFirstChildOfType(ASTPrimaryPrefix.class)
                .getFirstChildOfType(ASTName.class);
        if (methodName != null) {
            ClassScope classScope = node.getScope().getEnclosingScope(ClassScope.class);
            Map<MethodNameDeclaration, List<NameOccurrence>> methods = classScope.getMethodDeclarations();
            for (Map.Entry<MethodNameDeclaration, List<NameOccurrence>> e : methods.entrySet()) {
                if (e.getKey().getName().equals(methodName.getImage())) {
                    type = e.getKey().getNode().getFirstParentOfType(ASTMethodDeclaration.class)
                            .getFirstChildOfType(ASTResultType.class)
                            .getFirstDescendantOfType(ASTClassOrInterfaceType.class);
                    break;
                }
            }
        }
        return type;
    }

    private ASTClassOrInterfaceType getTypeOfPrimaryPrefix(ASTPrimarySuffix node) {
        return node.getParent().getFirstChildOfType(ASTPrimaryPrefix.class)
                .getFirstDescendantOfType(ASTClassOrInterfaceType.class);
    }
}
