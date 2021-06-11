/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class LiteralsFirstInComparisonsRule extends AbstractJavaRule {

    private static final String[] COMPARISON_OPS = {".equals", ".equalsIgnoreCase", ".compareTo", ".compareToIgnoreCase", ".contentEquals"};

    public LiteralsFirstInComparisonsRule() {
        addRuleChainVisit(ASTPrimaryExpression.class);
    }

    @Override
    public Object visit(ASTPrimaryExpression expression, Object data) {
        if (violatesLiteralsFirstInComparisonsRule(expression)) {
            addViolation(data, expression);
        }
        return data;
    }

    private boolean violatesLiteralsFirstInComparisonsRule(ASTPrimaryExpression expression) {
        return !hasStringLiteralFirst(expression) && isNullableComparisonWithStringLiteral(expression);
    }

    private boolean hasStringLiteralFirst(ASTPrimaryExpression expression) {
        ASTPrimaryPrefix primaryPrefix = expression.getFirstChildOfType(ASTPrimaryPrefix.class);
        ASTLiteral firstLiteral = primaryPrefix.getFirstChildOfType(ASTLiteral.class);
        return firstLiteral != null && firstLiteral.isStringLiteral();
    }

    private boolean isNullableComparisonWithStringLiteral(ASTPrimaryExpression expression) {
        String opName = getOperationName(expression);
        ASTName opTarget = getOperationTarget(expression);
        ASTPrimarySuffix argsSuffix = getSuffixOfArguments(expression);
        return opName != null && argsSuffix != null
            && isStringLiteralComparison(opName, argsSuffix)
            && isNotWithinNullComparison(expression)
            && !isConstantString(opTarget);
    }

    private String getOperationName(ASTPrimaryExpression primaryExpression) {
        return isMethodsChain(primaryExpression)
               ? getOperationNameBySuffix(primaryExpression)
               : getOperationNameByPrefix(primaryExpression);
    }

    private boolean isMethodsChain(ASTPrimaryExpression primaryExpression) {
        return primaryExpression.getNumChildren() > 2;
    }

    private ASTName getOperationTarget(ASTPrimaryExpression primaryExpression) {
        return isMethodsChain(primaryExpression)
                ? getOperationTargetBySuffix(primaryExpression)
                : getOperationTargetByPrefix(primaryExpression);
    }

    private String getOperationNameBySuffix(ASTPrimaryExpression primaryExpression) {
        ASTPrimarySuffix opAsSuffix = getPrimarySuffixAtIndexFromEnd(primaryExpression, 1);
        if (opAsSuffix != null) {
            String opName = opAsSuffix.getImage(); // name of pattern "operation"
            return "." + opName;
        }
        return null;
    }

    private ASTName getOperationTargetBySuffix(ASTPrimaryExpression primaryExpression) {
        ASTPrimarySuffix opAsSuffix = getPrimarySuffixAtIndexFromEnd(primaryExpression, 1);
        if (opAsSuffix != null) {
            return opAsSuffix.getFirstChildOfType(ASTName.class);
        }
        return null;
    }

    private String getOperationNameByPrefix(ASTPrimaryExpression primaryExpression) {
        ASTPrimaryPrefix opAsPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (opAsPrefix != null) {
            ASTName opName = opAsPrefix.getFirstChildOfType(ASTName.class); // name of pattern "*.operation"
            return opName != null ? opName.getImage() : null;
        }
        return null;
    }

    private ASTName getOperationTargetByPrefix(ASTPrimaryExpression primaryExpression) {
        ASTPrimaryPrefix opAsPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (opAsPrefix != null) {
            return opAsPrefix.getFirstChildOfType(ASTName.class); // name of pattern "*.operation"
        }
        return null;
    }

    private ASTPrimarySuffix getSuffixOfArguments(ASTPrimaryExpression primaryExpression) {
        return getPrimarySuffixAtIndexFromEnd(primaryExpression, 0);
    }

    private ASTPrimarySuffix getPrimarySuffixAtIndexFromEnd(ASTPrimaryExpression primaryExpression, int indexFromEnd) {
        int index = primaryExpression.getNumChildren() - 1 - indexFromEnd;
        if (index <= 0) {
            return null;
        }
        return (ASTPrimarySuffix) primaryExpression.getChild(index);
    }

    private boolean isStringLiteralComparison(String opName, ASTPrimarySuffix argsSuffix) {
        return isComparisonOperation(opName) && isSingleStringLiteralArgument(argsSuffix);
    }

    private boolean isComparisonOperation(String op) {
        for (String comparisonOp : COMPARISON_OPS) {
            if (op.endsWith(comparisonOp)) {
                return true;
            }
        }
        return false;
    }

    /*
     * This corresponds to the following XPath expression:
     * (../PrimarySuffix/Arguments/ArgumentList/Expression/PrimaryExpression/PrimaryPrefix/Literal[@StringLiteral= true()])
     *       and
     * ( count(../PrimarySuffix/Arguments/ArgumentList/Expression) = 1 )
     */
    private boolean isSingleStringLiteralArgument(ASTPrimarySuffix primarySuffix) {
        return isSingleArgumentSuffix(primarySuffix) && isStringLiteralFirstArgumentOfSuffix(primarySuffix);
    }

    private boolean isSingleArgumentSuffix(ASTPrimarySuffix primarySuffix) {
        return primarySuffix.getArgumentCount() == 1;
    }

    private boolean isStringLiteralFirstArgumentOfSuffix(ASTPrimarySuffix primarySuffix) {
        JavaNode argumentPrimaryPrefix = getArgumentPrimaryPrefix(primarySuffix);
        if (argumentPrimaryPrefix == null) {
            return false;
        }
        JavaNode firstLiteralArg = argumentPrimaryPrefix.getFirstChildOfType(ASTLiteral.class);
        JavaNode firstNameArg = argumentPrimaryPrefix.getFirstChildOfType(ASTName.class);
        return isStringLiteral(firstLiteralArg) || isConstantString(firstNameArg);
    }

    private JavaNode getArgumentPrimaryPrefix(ASTPrimarySuffix primarySuffix) {
        ASTExpression expression = primarySuffix.getFirstChildOfType(ASTArguments.class)
                                                .getFirstChildOfType(ASTArgumentList.class)
                                                .getFirstChildOfType(ASTExpression.class);

        assert expression != null : "We checked before that we had exactly one argument, so this cannot fail";

        ASTPrimaryExpression primaryExpression = expression.getFirstChildOfType(ASTPrimaryExpression.class);
        if (primaryExpression != null) {
            return primaryExpression.getChild(0);
        }
        return null;
    }

    private boolean isStringLiteral(JavaNode node) {
        if (node instanceof ASTLiteral) {
            ASTLiteral literal = (ASTLiteral) node;
            return literal.isStringLiteral();
        }
        return false;
    }

    private boolean isConstantString(JavaNode node) {
        if (node instanceof ASTName) {
            ASTName name = (ASTName) node;
            NameDeclaration resolved = name.getNameDeclaration();
            if (resolved instanceof VariableNameDeclaration
                && resolved.getNode() instanceof ASTVariableDeclaratorId) {
                ASTVariableDeclaratorId resolvedNode = (ASTVariableDeclaratorId) resolved.getNode();
                return resolvedNode.isFinal()
                    && resolvedNode.isField()
                    && resolvedNode.getFirstParentOfType(ASTFieldDeclaration.class).isStatic();
            } else if (resolved == null) {
                // try to resolve a referenced static field
                List<ASTImportDeclaration> imports = node.getRoot().findChildrenOfType(ASTImportDeclaration.class);
                Field field = tryResolve(name.getImage(), node.getRoot().getClassTypeResolver(), imports);
                if (field != null) {
                    return Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers());
                }
            }
        }
        return false;
    }

    private Field tryResolve(String fullPossibleClassName, ClassTypeResolver resolver, List<ASTImportDeclaration> imports) {
        Map<String, Class<?>> importedTypes = new HashMap<>();
        Set<String> onDemandImports = new HashSet<>();
        for (ASTImportDeclaration importDecl : imports) {
            if (importDecl.getType() != null) {
                importedTypes.put(importDecl.getType().getSimpleName(), importDecl.getType());
            } else if (importDecl.isImportOnDemand()) {
                onDemandImports.add(importDecl.getImportedName());
            }
        }

        String[] splitName = fullPossibleClassName.split("\\.");
        for (int i = splitName.length; i > 0; i--) {
            String possibleClassName = StringUtils.join(splitName, ".", 0, i);
            if (importedTypes.containsKey(possibleClassName)) {
                String possibleFieldName = splitName[i];
                Class<?> type = importedTypes.get(possibleClassName);
                try {
                    return type.getDeclaredField(possibleFieldName);
                } catch (ReflectiveOperationException ignored) {
                    // skip, try next
                }
            }
        }
        // try on-demand
        for (int i = splitName.length; i > 0; i--) {
            String possibleClassName = StringUtils.join(splitName, ".", 0, i);
            for (String prefix : onDemandImports) {
                Class<?> type = resolver.loadClassOrNull(prefix + "." + possibleClassName);
                if (type == null) {
                    continue;
                }
                
                String possibleFieldName = splitName[i];
                try {
                    return type.getDeclaredField(possibleFieldName);
                } catch (ReflectiveOperationException ignored) {
                    // skip, try next
                }
            }
        }
        return null;
    }

    private boolean isNotWithinNullComparison(ASTPrimaryExpression node) {
        return !isWithinNullComparison(node);
    }

    /*
     * Expression/ConditionalAndExpression//EqualityExpression(@Image='!=']//NullLiteral
     * Expression/ConditionalOrExpression//EqualityExpression(@Image='==']//NullLiteral
     */
    private boolean isWithinNullComparison(ASTPrimaryExpression node) {
        for (ASTExpression parentExpr : node.getParentsOfType(ASTExpression.class)) {
            if (isNullComparison(parentExpr)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNullComparison(ASTExpression expression) {
        return isAndNotNullComparison(expression) || isOrNullComparison(expression);
    }

    private boolean isAndNotNullComparison(ASTExpression expression) {
        ASTConditionalAndExpression andExpression = expression
                .getFirstChildOfType(ASTConditionalAndExpression.class);
        return andExpression != null && hasEqualityExpressionWithNullLiteral(andExpression, "!=");
    }

    private boolean isOrNullComparison(ASTExpression expression) {
        ASTConditionalOrExpression orExpression = expression
                .getFirstChildOfType(ASTConditionalOrExpression.class);
        return orExpression != null && hasEqualityExpressionWithNullLiteral(orExpression, "==");
    }

    private boolean hasEqualityExpressionWithNullLiteral(JavaNode node, String equalityOp) {
        ASTEqualityExpression equalityExpression = node.getFirstDescendantOfType(ASTEqualityExpression.class);
        if (equalityExpression != null && equalityExpression.hasImageEqualTo(equalityOp)) {
            return equalityExpression.hasDescendantOfType(ASTNullLiteral.class);
        }
        return false;
    }
}
