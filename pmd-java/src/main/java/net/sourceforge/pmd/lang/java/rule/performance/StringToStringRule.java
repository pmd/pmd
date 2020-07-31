/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public class StringToStringRule extends AbstractJavaRule {

    private final Map<String, Class<?>> declaredVariables = new HashMap<>();
    private final Set<ASTMethodDeclaration> methodsReturningString = new HashSet<>();

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
        declaredVariables.put(node.getName(), node.getType());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody body, Object data) {
        List<ASTMethodDeclaration> methodDeclarations = body.findDescendantsOfType(ASTMethodDeclaration.class);
        for (ASTMethodDeclaration methodDeclaration : methodDeclarations) {
            if (methodReturnsString(methodDeclaration)) {
                methodsReturningString.add(methodDeclaration);
            }
        }
        return super.visit(body, data);
    }

    private boolean methodReturnsString(ASTMethodDeclaration methodDeclaration) {
        ASTType returnType = methodDeclaration.getResultType().getFirstChildOfType(ASTType.class);
        return returnType != null && String.class.equals(returnType.getType());
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (isStringVariableDeclarator(node)) {
            for (NameOccurrence varUsage : node.getUsages()) {
                NameOccurrence qualifier = getVarUsageQualifier(varUsage);
                if (isToStringOnStringCall(node, qualifier)) {
                    addViolation(data, varUsage.getLocation());
                }
            }
        }
        return data;
    }

    private boolean isStringVariableDeclarator(ASTVariableDeclaratorId varDeclaratorId) {
        VariableNameDeclaration varNameDeclaration = varDeclaratorId.getNameDeclaration();
        return varNameDeclaration != null
                && TypeHelper.isExactlyAny(varNameDeclaration, String.class, String[].class);
    }

    private NameOccurrence getVarUsageQualifier(NameOccurrence varUsage) {
        JavaNameOccurrence jVarUsage = (JavaNameOccurrence) varUsage;
        return jVarUsage.getNameForWhichThisIsAQualifier();
    }

    private boolean isToStringOnStringCall(ASTVariableDeclaratorId varDeclaratorId, NameOccurrence qualifier) {
        if (qualifier != null) {
            return isNotAMethodReference(qualifier) && isNotAnArrayField(varDeclaratorId, qualifier)
                    && isToString(qualifier.getImage());
        }
        return false;
    }

    private boolean isNotAnArrayField(ASTVariableDeclaratorId varDeclaratorId, NameOccurrence qualifier) {
        return !varDeclaratorId.hasArrayType() || isNotAName(qualifier);
    }

    private boolean isNotAMethodReference(NameOccurrence qualifier) {
        return isNotA(qualifier, ASTMethodReference.class);
    }

    private boolean isNotAName(NameOccurrence qualifier) {
        return isNotA(qualifier, ASTName.class);
    }

    private boolean isNotA(NameOccurrence qualifier, Class<? extends JavaNode> type) {
        ScopedNode location = qualifier.getLocation();
        return location == null || !type.isAssignableFrom(location.getClass());
    }

    @Override
    public Object visit(ASTPrimaryExpression primaryExpr, Object data) {
        if (callsToStringOnMethodReturningString(primaryExpr)) {
            addViolation(data, primaryExpr);
        }
        return super.visit(primaryExpr, data);
    }

    private boolean callsToStringOnMethodReturningString(ASTPrimaryExpression primaryExpr) {
        return doesSrcMethodReturnString(primaryExpr) && hasToStringCall(primaryExpr);
    }

    private boolean doesSrcMethodReturnString(ASTPrimaryExpression primaryExpr) {
        String srcMethodName = getSrcMethodName(primaryExpr);
        ASTArguments srcMethodArgs = primaryExpr.getFirstDescendantOfType(ASTArguments.class);
        if (srcMethodArgs != null) {
            for (ASTMethodDeclaration methodReturningString : methodsReturningString) {
                if (methodReturningString.getName().equals(srcMethodName)
                        && areArgsValidForMethod(srcMethodArgs, methodReturningString)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getSrcMethodName(ASTPrimaryExpression primaryExpr) {
        ASTPrimaryPrefix primaryPrefix = primaryExpr.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        if (hasNoModifiers(primaryPrefix)) {
            ASTName name = primaryPrefix.getFirstDescendantOfType(ASTName.class);
            return name != null ? name.getImage() : null;
        }
        ASTPrimarySuffix primarySuffix = primaryExpr.getFirstDescendantOfType(ASTPrimarySuffix.class);
        return primarySuffix != null ? primarySuffix.getImage() : null;
    }

    private boolean hasNoModifiers(ASTPrimaryPrefix primaryPrefix) {
        return !primaryPrefix.usesThisModifier() && !primaryPrefix.usesSuperModifier();
    }

    private boolean areArgsValidForMethod(ASTArguments args, ASTMethodDeclaration methodDeclaration) {
        if (args.size() == methodDeclaration.getArity()) {
            ASTArgumentList argsList = args.getFirstChildOfType(ASTArgumentList.class);
            return argsList == null || argsMatchMethodParams(argsList, methodDeclaration);
        }
        return false;
    }

    private boolean argsMatchMethodParams(ASTArgumentList argsList, ASTMethodDeclaration methodDeclaration) {
        Iterator<? extends JavaNode> argsIterator = argsList.children().iterator();
        ASTFormalParameters methodParams = methodDeclaration.getFormalParameters();
        for (ASTFormalParameter methodParam : methodParams) {
            if (argNotMatchesMethodParam(argsIterator.next(), methodParam)) {
                return false;
            }
        }
        return true;
    }

    private boolean argNotMatchesMethodParam(JavaNode arg, ASTFormalParameter methodParam) {
        return !argMatchesMethodParam(arg, methodParam);
    }

    private boolean argMatchesMethodParam(JavaNode arg, ASTFormalParameter methodParam) {
        Class<?> argType = getArgumentType(arg);
        Class<?> paramType = methodParam.getType();
        return argType != null && paramType.isAssignableFrom(argType);
    }

    private Class<?> getArgumentType(JavaNode arg) {
        ASTLiteral literalArg = arg.getFirstDescendantOfType(ASTLiteral.class);
        if (literalArg == null) {
            ASTName varName = arg.getFirstDescendantOfType(ASTName.class);
            return varName != null ? declaredVariables.get(varName.getImage()) : null;
        }
        return literalArg.getType();
    }

    private boolean hasToStringCall(ASTPrimaryExpression primaryExpr) {
        List<ASTPrimarySuffix> methodCalls = primaryExpr.findDescendantsOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix methodCall : methodCalls) {
            String methodName = methodCall.getImage();
            if (isToString(methodName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isToString(String methodName) {
        return "toString".equals(methodName);
    }
}
