/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Makes sure you close your database connections. It does this by looking for
 * code patterned like this:
 *
 * <pre>
 *  Connection c = X;
 *  try {
 *   // do stuff, and maybe catch something
 *  } finally {
 *   c.close();
 *  }
 * </pre>
 *
 *  @author original author unknown
 *  @author Contribution from Pierre Mathien
 */
public class CloseResourceRule extends AbstractJavaRule {

    private final Set<String> types = new HashSet<>();
    private final Set<String> simpleTypes = new HashSet<>();

    private final Set<String> closeTargets = new HashSet<>();
    private static final PropertyDescriptor<List<String>> CLOSE_TARGETS_DESCRIPTOR =
            stringListProperty("closeTargets")
                           .desc("Methods which may close this resource")
                           .emptyDefaultValue()
                           .delim(',').build();

    private static final PropertyDescriptor<List<String>> TYPES_DESCRIPTOR =
            stringListProperty("types")
                    .desc("Affected types")
                    .defaultValues("java.lang.AutoCloseable", "java.sql.Connection", "java.sql.Statement", "java.sql.ResultSet")
                    .delim(',').build();

    private static final PropertyDescriptor<Boolean> USE_CLOSE_AS_DEFAULT_TARGET =
            booleanProperty("closeAsDefaultTarget")
                    .desc("Consider 'close' as a target by default").defaultValue(true).build();

    private static final PropertyDescriptor<List<String>> ALLOWED_RESOURCE_TYPES =
            stringListProperty("allowedResourceTypes")
            .desc("Exact class names that do not need to be closed")
            .defaultValues("java.io.ByteArrayOutputStream", "java.io.ByteArrayInputStream", "java.io.StringWriter",
                    "java.io.CharArrayWriter", "java.util.stream.Stream", "java.util.stream.IntStream", "java.util.stream.LongStream",
                    "java.util.stream.DoubleStream")
            .build();


    public CloseResourceRule() {
        definePropertyDescriptor(CLOSE_TARGETS_DESCRIPTOR);
        definePropertyDescriptor(TYPES_DESCRIPTOR);
        definePropertyDescriptor(USE_CLOSE_AS_DEFAULT_TARGET);
        definePropertyDescriptor(ALLOWED_RESOURCE_TYPES);
    }

    @Override
    public void start(RuleContext ctx) {
        closeTargets.clear();
        simpleTypes.clear();
        types.clear();

        if (getProperty(CLOSE_TARGETS_DESCRIPTOR) != null) {
            closeTargets.addAll(getProperty(CLOSE_TARGETS_DESCRIPTOR));
        }
        if (getProperty(USE_CLOSE_AS_DEFAULT_TARGET)) {
            closeTargets.add("close");
        }
        if (getProperty(TYPES_DESCRIPTOR) != null) {
            types.addAll(getProperty(TYPES_DESCRIPTOR));
            for (String type : getProperty(TYPES_DESCRIPTOR)) {
                simpleTypes.add(toSimpleType(type));
            }
        }
    }

    private static String toSimpleType(String fullyQualifiedClassName) {
        int lastIndexOf = fullyQualifiedClassName.lastIndexOf('.');
        if (lastIndexOf > -1) {
            return fullyQualifiedClassName.substring(lastIndexOf + 1);
        } else {
            return fullyQualifiedClassName;
        }
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        checkForResources(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        checkForResources(node, data);
        return super.visit(node, data);
    }

    private void checkForResources(ASTMethodOrConstructorDeclaration node, Object data) {
        Map<ASTVariableDeclarator, TypeNode> resVars = getResourceVariables(node);
        for (Map.Entry<ASTVariableDeclarator, TypeNode> resVarEntry : resVars.entrySet()) {
            TypeNode resVarType = resVarEntry.getValue();
            ASTVariableDeclarator resVar = resVarEntry.getKey();
            if (isNotAllowedResourceType(resVarType) && isNotWrappingResourceMethodParameter(resVar, node)
                    && isResourceVariableUnclosed(resVar)) {
                addCloseResourceViolation(resVar.getVariableId(), resVarType, data);
            }
        }
    }

    private Map<ASTVariableDeclarator, TypeNode> getResourceVariables(ASTMethodOrConstructorDeclaration method) {
        List<ASTVariableDeclarator> vars = method.findDescendantsOfType(ASTVariableDeclarator.class);
        Map<ASTVariableDeclarator, TypeNode> resVars = new HashMap<>();
        for (ASTVariableDeclarator var : vars) {
            TypeNode varType = getTypeOfVariable(var);
            if (isResourceTypeOrSubtype(varType)) {
                resVars.put(var, wrappedResourceTypeOrReturn(var, varType));
            }
        }
        return resVars;
    }

    private TypeNode getTypeOfVariable(ASTVariableDeclarator var) {
        TypeNode declaredVarType = getDeclaredTypeOfVariable(var);
        if (var.hasInitializer()) {
            TypeNode runtimeVarType = getRuntimeTypeOfVariable(var);
            return runtimeVarType != null ? runtimeVarType : declaredVarType;
        }
        return declaredVarType;
    }

    private TypeNode getDeclaredTypeOfVariable(ASTVariableDeclarator var) {
        ASTLocalVariableDeclaration localVar = (ASTLocalVariableDeclaration) var.getParent();
        return localVar.getTypeNode();
    }

    private TypeNode getRuntimeTypeOfVariable(ASTVariableDeclarator var) {
        ASTExpression initExpr = var.getInitializer().getFirstChildOfType(ASTExpression.class);
        if (initExpr != null && isNotMethodCall(initExpr)) {
            return initExpr.getType() != null ? initExpr : null;
        }
        return null;
    }

    private boolean isNotMethodCall(ASTExpression expr) {
        return !isMethodCall(expr);
    }

    private boolean isMethodCall(ASTExpression expression) {
        return expression != null && expression.getNumChildren() > 0
                && expression.getChild(0).getFirstChildOfType(ASTPrimarySuffix.class) != null;
    }

    private TypeNode wrappedResourceTypeOrReturn(ASTVariableDeclarator var, TypeNode defaultVal) {
        if (var.hasInitializer()) {
            TypeNode wrappedResType = getWrappedResourceType(var);
            return wrappedResType != null ? wrappedResType : defaultVal;
        }
        return defaultVal;
    }

    private TypeNode getWrappedResourceType(ASTVariableDeclarator var) {
        ASTExpression initExpr = var.getInitializer().getFirstChildOfType(ASTExpression.class);
        if (initExpr != null) {
            ASTExpression wrappedType = getAllocationFirstArgumentVariable(initExpr);
            return wrappedType != null && isResourceTypeOrSubtype(wrappedType)
                    ? wrappedType
                    : null;
        }
        return null;
    }

    private ASTExpression getAllocationFirstArgumentVariable(ASTExpression expression) {
        ASTAllocationExpression allocation = getLastAllocationExpression(expression);
        if (allocation != null) {
            ASTArgumentList argsList = allocation.getFirstDescendantOfType(ASTArgumentList.class);
            if (argsList != null) {
                ASTExpression firstArg = argsList.getFirstChildOfType(ASTExpression.class);
                return isNotLiteral(firstArg) ? firstArg : null;
            }
        }
        return null;
    }

    private ASTAllocationExpression getLastAllocationExpression(ASTExpression expression) {
        List<ASTAllocationExpression> allocations = expression.findDescendantsOfType(ASTAllocationExpression.class);
        if (!allocations.isEmpty()) {
            int lastAllocationIndex = allocations.size() - 1;
            return allocations.get(lastAllocationIndex);
        }
        return null;
    }

    private boolean isNotLiteral(ASTExpression expression) {
        ASTLiteral literal = expression.getFirstDescendantOfType(ASTLiteral.class);
        return literal == null;
    }

    private boolean isNotAllowedResourceType(TypeNode varType) {
        return !isAllowedResourceType(varType);
    }

    private boolean isAllowedResourceType(TypeNode refType) {
        List<String> allowedResourceTypes = getProperty(ALLOWED_RESOURCE_TYPES);
        if (allowedResourceTypes != null) {
            for (String type : allowedResourceTypes) {
                // the check here must be a exact type match, since subclasses may override close()
                // and actually require closing
                if (TypeHelper.isExactlyA(refType, type)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotWrappingResourceMethodParameter(ASTVariableDeclarator var,
            ASTMethodOrConstructorDeclaration method) {
        return !isWrappingResourceMethodParameter(var, method);
    }

    /**
     * Checks whether the variable is resource and initialized from a method parameter.
     * @param var the resource variable that is being initialized
     * @param method the method or constructor in which the variable is declared
     * @return <code>true</code> if the variable is resource and initialized from a method parameter. <code>false</code>
     *         otherwise.
     */
    private boolean isWrappingResourceMethodParameter(ASTVariableDeclarator var, ASTMethodOrConstructorDeclaration method) {
        String wrappedVarName = getWrappedVariableName(var);
        if (wrappedVarName != null) {
            ASTFormalParameters methodParams = method.getFirstDescendantOfType(ASTFormalParameters.class);
            if (methodParams != null) {
                List<ASTVariableDeclaratorId> ids = methodParams.findDescendantsOfType(ASTVariableDeclaratorId.class);
                for (ASTVariableDeclaratorId id : ids) {
                    if (id.hasImageEqualTo(wrappedVarName) && isResourceTypeOrSubtype(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getWrappedVariableName(ASTVariableDeclarator var) {
        if (var.hasInitializer()) {
            ASTName varName = var.getInitializer().getFirstDescendantOfType(ASTName.class);
            return varName != null ? varName.getImage() : null;
        }
        return null;
    }

    private boolean isResourceTypeOrSubtype(TypeNode refType) {
        return refType.getType() != null
                ? isNodeInstanceOfResourceType(refType)
                : nodeHasReferenceToResourceType(refType);
    }

    private boolean isNodeInstanceOfResourceType(TypeNode refType) {
        for (String resType : types) {
            if (TypeHelper.isA(refType, resType)) {
                return true;
            }
        }
        return false;
    }

    private boolean nodeHasReferenceToResourceType(TypeNode refType) {
        ASTClassOrInterfaceType type = refType.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return type != null && isResourceTypeName(type.getImage()) && !type.isReferenceToClassSameCompilationUnit();
    }

    private boolean isResourceTypeName(String typeName) {
        String simpleTypeName = toSimpleType(typeName);
        return types.contains(typeName) || simpleTypes.contains(simpleTypeName);
    }

    private boolean isResourceVariableUnclosed(ASTVariableDeclarator var) {
        return !isResourceVariableClosed(var);
    }

    private boolean isResourceVariableClosed(ASTVariableDeclarator var) {
        Node methodOfVar = getMethodOfNode(var);
        return hasTryStatementClosingResourceVariable(methodOfVar, var)
                || isReturnedByMethod(var.getName(), methodOfVar);
    }

    private Node getMethodOfNode(Node node) {
        Node parent = node.getParent();
        while (isNotMethod(parent)) {
            parent = parent.getParent();
        }
        return parent;
    }

    private boolean isNotMethod(Node node) {
        return !(node instanceof ASTBlock || node instanceof ASTConstructorDeclaration);
    }

    private boolean hasTryStatementClosingResourceVariable(Node node, ASTVariableDeclarator var) {
        List<ASTTryStatement> tryStatements = node.findDescendantsOfType(ASTTryStatement.class, true);
        for (ASTTryStatement tryStatement : tryStatements) {
            if (tryStatementClosesResourceVariable(tryStatement, var)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryStatementClosesResourceVariable(ASTTryStatement tryStatement, ASTVariableDeclarator var) {
        if (tryStatement.getBeginLine() >= var.getBeginLine() && noneCriticalStatementsBetween(var, tryStatement)) {
            if (hasFinallyClause(tryStatement)) {
                ASTBlock finallyBody = tryStatement.getFinallyClause().getBody();
                return blockClosesResourceVariable(finallyBody, var.getName());
            } else if (tryStatement.isTryWithResources()) {
                return isVariableSpecifiedInTryWithResource(var.getName(), tryStatement);
            }
        }
        return false;
    }

    private boolean noneCriticalStatementsBetween(ASTVariableDeclarator var, ASTTryStatement tryStatement) {
        return !anyCriticalStatementBetween(var, tryStatement);
    }

    private boolean anyCriticalStatementBetween(ASTVariableDeclarator var, ASTTryStatement tryStatement) {
        ASTBlockStatement varBlockStatement = var.getFirstParentOfType(ASTBlockStatement.class);
        ASTBlockStatement tryBlockStatement = tryStatement.getFirstParentOfType(ASTBlockStatement.class);
        if (isNotNullInitialized(var) && areStatementsOfSameBlock(varBlockStatement, tryBlockStatement)) {
            for (ASTBlockStatement bsBetween : getBlockStatementsBetween(varBlockStatement, tryBlockStatement)) {
                if (isCriticalStatement(bsBetween)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotNullInitialized(ASTVariableDeclarator var) {
        return !hasNullInitializer(var);
    }

    private boolean hasNullInitializer(ASTVariableDeclarator var) {
        if (var.hasInitializer()) {
            ASTPrimaryPrefix primaryPrefix = var.getInitializer().getFirstDescendantOfType(ASTPrimaryPrefix.class);
            return primaryPrefix != null && primaryPrefix.hasDescendantOfType(ASTNullLiteral.class);
        }
        return false;
    }

    private boolean areStatementsOfSameBlock(ASTBlockStatement bs0, ASTBlockStatement bs1) {
        return bs0.getParent() == bs1.getParent();
    }

    private List<ASTBlockStatement> getBlockStatementsBetween(ASTBlockStatement top, ASTBlockStatement bottom) {
        List<ASTBlockStatement> blockStatements = top.getParent().findChildrenOfType(ASTBlockStatement.class);
        int topBSIndex = blockStatements.indexOf(top);
        int bottomBSIndex = blockStatements.indexOf(bottom);
        return blockStatements.subList(topBSIndex + 1, bottomBSIndex);
    }

    private boolean isCriticalStatement(ASTBlockStatement blockStatement) {
        boolean isVarDeclaration = blockStatement.hasDescendantOfType(ASTLocalVariableDeclaration.class);
        boolean isAssignmentOperator = blockStatement.hasDescendantOfType(ASTAssignmentOperator.class);
        return !isVarDeclaration && !isAssignmentOperator;
    }

    private boolean hasFinallyClause(ASTTryStatement tryStatement) {
        return tryStatement.getFinallyClause() != null;
    }

    private boolean blockClosesResourceVariable(ASTBlock block, String variableToClose) {
        return hasNotConditionalCloseCallOnVariable(block, variableToClose)
                || hasMethodCallClosingResourceVariable(block, variableToClose);
    }

    private boolean hasNotConditionalCloseCallOnVariable(ASTBlock block, String variableToClose) {
        List<ASTName> operations = block.findDescendantsOfType(ASTName.class);
        for (ASTName operation : operations) {
            if (isCloseCallOnVariable(operation, variableToClose)
                    && isNotConditional(block, operation, variableToClose)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCloseCallOnVariable(ASTName op, String variableToClose) {
        String opName = op.getImage();
        if (opName != null && opName.contains(".")) {
            String[] parts = opName.split("\\.");
            if (parts.length == 2) {
                String methodName = parts[1];
                String varName = parts[0];
                return varName.equals(variableToClose) && closeTargets.contains(methodName);
            }
        }
        return false;
    }

    /**
     * Checks, whether the given node is inside an if condition, and if so,
     * whether this is a null check for the given varName.
     *
     * @param enclosingBlock
     *            where to search for if statements
     * @param node
     *            the node, where the call for the close is done
     * @param varName
     *            the variable, that is maybe null-checked
     * @return <code>true</code> if no if condition is involved or if the if
     *         condition is a null-check.
     */
    private boolean isNotConditional(ASTBlock enclosingBlock, Node node, String varName) {
        ASTIfStatement ifStatement = findIfStatement(enclosingBlock, node);
        if (ifStatement != null) {
            try {
                // find expressions like: varName != null or null != varName
                List<?> nodes = ifStatement.findChildNodesWithXPath("Expression/EqualityExpression[@Image='!=']"
                        + "  [PrimaryExpression/PrimaryPrefix/Name[@Image='" + varName + "']]"
                        + "  [PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]");
                return !nodes.isEmpty();
            } catch (JaxenException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private ASTIfStatement findIfStatement(ASTBlock enclosingBlock, Node node) {
        ASTIfStatement ifStatement = node.getFirstParentOfType(ASTIfStatement.class);
        List<ASTIfStatement> allIfStatements = enclosingBlock.findDescendantsOfType(ASTIfStatement.class);
        if (ifStatement != null && allIfStatements.contains(ifStatement)) {
            return ifStatement;
        }
        return null;
    }

    private boolean hasMethodCallClosingResourceVariable(ASTBlock block, String variableToClose) {
        List<ASTPrimaryExpression> expressions = block.findDescendantsOfType(ASTPrimaryExpression.class, true);
        for (ASTPrimaryExpression expression : expressions) {
            if (isMethodCallClosingResourceVariable(expression, variableToClose)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodCallClosingResourceVariable(ASTPrimaryExpression expression, String variableToClose) {
        ASTPrimaryPrefix prefix = expression.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTPrimarySuffix suffix = expression.getFirstDescendantOfType(ASTPrimarySuffix.class);
        if (prefix != null && suffix != null) {
            return (isCloseTargetMethodCall(prefix, suffix) || hasChainedCloseTargetMethodCall(expression))
                    && variableIsPassedToMethod(variableToClose, expression);
        }
        return false;
    }

    private boolean isCloseTargetMethodCall(ASTPrimaryPrefix prefix, ASTPrimarySuffix suffix) {
        String methodCall = getMethodCallStr(prefix, suffix);
        return methodCall != null && closeTargets.contains(methodCall);
    }

    private String getMethodCallStr(ASTPrimaryPrefix prefix, ASTPrimarySuffix suffix) {
        if (prefix.getImage() == null) {
            ASTName name = prefix.getFirstDescendantOfType(ASTName.class);
            return name != null ? name.getImage() : null;
        } else if (suffix.getImage() != null) {
            return prefix.getImage() + "." + suffix.getImage();
        }
        return null;
    }

    private boolean hasChainedCloseTargetMethodCall(ASTPrimaryExpression expr) {
        List<ASTPrimarySuffix> methodCalls = expr.findDescendantsOfType(ASTPrimarySuffix.class, true);
        for (ASTPrimarySuffix methodCall : methodCalls) {
            if (closeTargets.contains(methodCall.getImage())) {
                return true;
            }
        }
        return false;
    }

    private boolean variableIsPassedToMethod(String varName, ASTPrimaryExpression methodCall) {
        List<ASTName> methodCallArgs = methodCall.findDescendantsOfType(ASTName.class, true);
        for (ASTName methodCallArg : methodCallArgs) {
            if (isMethodCallArgument(methodCallArg) && methodCallArg.hasImageEqualTo(varName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodCallArgument(ASTName varName) {
        return varName.getFirstParentOfType(ASTArgumentList.class) != null;
    }

    private boolean isVariableSpecifiedInTryWithResource(String varName, ASTTryStatement tryWithResource) {
        List<ASTName> specifiedResources = tryWithResource.getFirstChildOfType(ASTResourceSpecification.class)
                .findDescendantsOfType(ASTName.class);
        for (ASTName res : specifiedResources) {
            if (res.hasImageEqualTo(varName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReturnedByMethod(String varName, Node method) {
        List<ASTReturnStatement> returns = method.findDescendantsOfType(ASTReturnStatement.class, true);
        for (ASTReturnStatement returnStatement : returns) {
            ASTName name = returnStatement.getFirstDescendantOfType(ASTName.class);
            if (name != null && name.hasImageEqualTo(varName)) {
                return true;
            }
        }
        return false;
    }

    private void addCloseResourceViolation(ASTVariableDeclaratorId id, TypeNode type, Object data) {
        Class<?> typeClass = type.getType();
        if (typeClass != null) {
            addViolation(data, id, typeClass.getSimpleName());
        } else {
            addViolation(data, id, id.getTypeNode().getTypeImage());
        }
    }
}
