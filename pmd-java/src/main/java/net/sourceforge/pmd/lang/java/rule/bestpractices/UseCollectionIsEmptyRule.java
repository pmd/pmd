/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
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
        return occ.getNameForWhichThisIsAQualifier() != null
                && occ.getLocation().getImage().endsWith(".size");
    }

    @Override
    public Map<String, List<String>> getComparisonTargets() {
        List<String> zeroAndOne = asList("0", "1");
        List<String> zero = singletonList("0");
        Map<String, List<String>> rules = new HashMap<>();
        rules.put("<", zeroAndOne);
        rules.put(">", zero);
        rules.put("==", zero);
        rules.put("!=", zero);
        rules.put(">=", zeroAndOne);
        rules.put("<=", zero);
        return rules;
    }

    @Override
    public Object visit(ASTPrimarySuffix node, Object data) {
        if (isSizeMethodCall(node) && isCalledOnCollection(node)) {
            Node expr = node.getParent().getParent();
            checkNodeAndReport(data, node, expr);
        }
        return data;
    }

    private boolean isSizeMethodCall(ASTPrimarySuffix primarySuffix) {
        String calledMethodName = primarySuffix.getImage();
        return calledMethodName != null && calledMethodName.endsWith("size");
    }

    private boolean isCalledOnCollection(ASTPrimarySuffix primarySuffix) {
        ASTClassOrInterfaceType calledOnType = getTypeOfVariable(primarySuffix);
        if (calledOnType == null) {
            calledOnType = getTypeOfMethodCall(primarySuffix);
        }
        return calledOnType != null && TypeHelper.isA(calledOnType, Collection.class);
    }

    private ASTClassOrInterfaceType getTypeOfVariable(ASTPrimarySuffix primarySuffix) {
        ASTPrimaryExpression primaryExpression = primarySuffix.getFirstParentOfType(ASTPrimaryExpression.class);
        ASTPrimaryPrefix varPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (prefixWithNoModifiers(varPrefix)) {
            return varPrefix.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        }
        String varName = getVariableNameBySuffix(primaryExpression);
        return varName != null ? getTypeOfVariableByName(varName, primaryExpression) : null;
    }

    private boolean prefixWithNoModifiers(ASTPrimaryPrefix primaryPrefix) {
        return !primaryPrefix.usesSuperModifier() && !primaryPrefix.usesThisModifier();
    }

    private String getVariableNameBySuffix(ASTPrimaryExpression primaryExpression) {
        ASTPrimarySuffix varSuffix = primaryExpression
                .getFirstChildOfType(ASTPrimarySuffix.class);
        return varSuffix.getImage();
    }

    private ASTClassOrInterfaceType getTypeOfVariableByName(String varName, ASTPrimaryExpression expr) {
        ASTClassOrInterfaceBody classBody = expr.getFirstParentOfType(ASTClassOrInterfaceBody.class);
        List<ASTVariableDeclarator> varDeclarators = classBody.findDescendantsOfType(ASTVariableDeclarator.class);
        for (ASTVariableDeclarator varDeclarator : varDeclarators) {
            if (varDeclarator.getName().equals(varName)) {
                return varDeclarator.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
            }
        }
        return null;
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
}
