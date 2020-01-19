/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
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
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
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

    private Set<String> types = new HashSet<>();
    private Set<String> simpleTypes = new HashSet<>();

    private Set<String> closeTargets = new HashSet<>();
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
                    "java.io.CharArrayWriter", "java.util.stream.Stream")
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
        if (getProperty(USE_CLOSE_AS_DEFAULT_TARGET) && !closeTargets.contains("close")) {
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
        List<ASTLocalVariableDeclaration> localVars = node.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        List<ASTVariableDeclarator> vars = new ArrayList<>();
        Map<ASTVariableDeclaratorId, TypeNode> ids = new HashMap<>();

        // find all variable declarators
        for (ASTLocalVariableDeclaration localVar : localVars) {
            vars.addAll(localVar.findChildrenOfType(ASTVariableDeclarator.class));
        }

        // find all variable references to Connection objects
        for (ASTVariableDeclarator var : vars) {
            // get the type of the local var declaration
            TypeNode type = ((ASTLocalVariableDeclaration) var.getParent()).getTypeNode();

            if (type != null && isResourceTypeOrSubtype(type)) {
                if (var.hasInitializer()) {
                    // figure out the runtime type. If the variable is initialized, take the type from there
                    ASTExpression expression = var.getInitializer().getFirstChildOfType(ASTExpression.class);
                    TypeNode runtimeType = expression;
                    if (!isMethodCall(expression) && runtimeType != null && runtimeType.getType() != null) {
                        type = runtimeType;
                    }

                    // consider cases, when the streams are chained
                    // assumes, that the underlaying stream is always the first argument in the
                    // constructor call.
                    ASTExpression firstArgument = getAllocationFirstArgument(expression);
                    if (firstArgument != null) {
                        type = firstArgument;
                    }
                }

                if (!isAllowedResourceType(type) && !isMethodParameter(var, node)) {
                    ids.put(var.getVariableId(), type);
                }
            }
        }

        // if there are closables, ensure each is closed.
        for (Map.Entry<ASTVariableDeclaratorId, TypeNode> entry : ids.entrySet()) {
            ASTVariableDeclaratorId variableId = entry.getKey();
            ensureClosed((ASTLocalVariableDeclaration) variableId.getParent().getParent(), variableId,
                    entry.getValue(), data);
        }
    }

    /**
     * Checks whether the variable is initialized from a method parameter.
     * @param var the variable that is being initialized
     * @param methodOrCstor the method or constructor in which the variable is declared
     * @return <code>true</code> if the variable is initialized from a method parameter. <code>false</code>
     *         otherwise.
     */
    private boolean isMethodParameter(ASTVariableDeclarator var, ASTMethodOrConstructorDeclaration methodOrCstor) {
        if (!var.hasInitializer()) {
            return false;
        }

        boolean result = false;
        ASTVariableInitializer initializer = var.getInitializer();
        ASTName name = initializer.getFirstDescendantOfType(ASTName.class);
        if (name != null) {
            ASTFormalParameters formalParameters = null;
            if (methodOrCstor instanceof ASTMethodDeclaration) {
                formalParameters = ((ASTMethodDeclaration) methodOrCstor).getFormalParameters();
            } else if (methodOrCstor instanceof ASTConstructorDeclaration) {
                formalParameters = ((ASTConstructorDeclaration) methodOrCstor).getFormalParameters();
            }
            if (formalParameters != null) {
                List<ASTVariableDeclaratorId> ids = formalParameters.findDescendantsOfType(ASTVariableDeclaratorId.class);
                for (ASTVariableDeclaratorId id : ids) {
                    if (id.hasImageEqualTo(name.getImage()) && isResourceTypeOrSubtype(id)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private ASTExpression getAllocationFirstArgument(ASTExpression expression) {
        List<ASTAllocationExpression> allocations = expression.findDescendantsOfType(ASTAllocationExpression.class);
        ASTExpression firstArgument = null;

        if (!allocations.isEmpty()) {
            ASTArgumentList argumentList = allocations.get(allocations.size() - 1).getFirstDescendantOfType(ASTArgumentList.class);
            if (argumentList != null) {
                firstArgument = argumentList.getFirstChildOfType(ASTExpression.class);
            }
        }

        // the argument must not be a literal, it needs to be a Name referring to a variable
        if (firstArgument != null && firstArgument.getFirstDescendantOfType(ASTName.class) != null) {
            ASTName name = firstArgument.getFirstDescendantOfType(ASTName.class);

            Map<VariableNameDeclaration, List<NameOccurrence>> vars = firstArgument.getScope()
                    .getDeclarations(VariableNameDeclaration.class);
            for (VariableNameDeclaration nameDecl : vars.keySet()) {
                if (nameDecl.getName().equals(name.getImage()) && isResourceTypeOrSubtype(firstArgument)) {
                    return firstArgument;
                }
            }
        }
        return null;
    }

    private boolean isMethodCall(ASTExpression expression) {
        return expression != null
             && expression.getNumChildren() > 0
             && expression.getChild(0) instanceof ASTPrimaryExpression
             && expression.getChild(0).getFirstChildOfType(ASTPrimarySuffix.class) != null;
    }

    private boolean isResourceTypeOrSubtype(TypeNode refType) {
        if (refType.getType() != null) {
            for (String type : types) {
                if (TypeHelper.isA(refType, type)) {
                    return true;
                }
            }
        } else if (refType.getNumChildren() > 0 && refType.getChild(0) instanceof ASTReferenceType) {
            // no type information (probably missing auxclasspath) - use simple types
            ASTReferenceType ref = (ASTReferenceType) refType.getChild(0);
            if (ref.getChild(0) instanceof ASTClassOrInterfaceType) {
                ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.getChild(0);
                if (simpleTypes.contains(toSimpleType(clazz.getImage())) && !clazz.isReferenceToClassSameCompilationUnit()
                        || types.contains(clazz.getImage()) && !clazz.isReferenceToClassSameCompilationUnit()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAllowedResourceType(TypeNode refType) {
        List<String> allowedResourceTypes = getProperty(ALLOWED_RESOURCE_TYPES);
        if (refType.getType() != null && allowedResourceTypes != null) {
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

    private boolean hasNullInitializer(ASTLocalVariableDeclaration var) {
        ASTVariableInitializer init = var.getFirstDescendantOfType(ASTVariableInitializer.class);
        if (init != null) {
            try {
                List<?> nulls = init
                        .findChildNodesWithXPath("Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral");
                return !nulls.isEmpty();
            } catch (JaxenException e) {
                return false;
            }
        }
        return false;
    }

    private void ensureClosed(ASTLocalVariableDeclaration var, ASTVariableDeclaratorId id, TypeNode type, Object data) {
        // What are the chances of a Connection being instantiated in a
        // for-loop init block? Anyway, I'm lazy!
        String variableToClose = id.getImage();
        Node n = var;

        while (!(n instanceof ASTBlock) && !(n instanceof ASTConstructorDeclaration)) {
            n = n.getParent();
        }

        Node top = n;

        List<ASTTryStatement> tryblocks = top.findDescendantsOfType(ASTTryStatement.class);

        boolean closed = false;

        ASTBlockStatement parentBlock = id.getFirstParentOfType(ASTBlockStatement.class);

        // look for try blocks below the line the variable was
        // introduced and make sure there is a .close call in a finally
        // block.
        for (ASTTryStatement t : tryblocks) {

            // verifies that there are no critical statements between the
            // variable declaration and
            // the beginning of the try block.
            ASTBlockStatement tryBlock = t.getFirstParentOfType(ASTBlockStatement.class);
            // no need to check for critical statements, if
            // the variable has been initialized with null
            if (!hasNullInitializer(var) && parentBlock.getParent() == tryBlock.getParent()) {

                List<ASTBlockStatement> blocks = parentBlock.getParent().findChildrenOfType(ASTBlockStatement.class);
                int parentBlockIndex = blocks.indexOf(parentBlock);
                int tryBlockIndex = blocks.indexOf(tryBlock);
                boolean criticalStatements = false;

                for (int i = parentBlockIndex + 1; i < tryBlockIndex; i++) {
                    // assume variable declarations are not critical and assignments are not critical
                    ASTBlockStatement block = blocks.get(i);
                    ASTLocalVariableDeclaration varDecl = block
                            .getFirstDescendantOfType(ASTLocalVariableDeclaration.class);
                    ASTStatementExpression statementExpression = block.getFirstDescendantOfType(ASTStatementExpression.class);

                    if (varDecl == null && (statementExpression == null
                            || statementExpression.getFirstChildOfType(ASTAssignmentOperator.class) == null)) {
                        criticalStatements = true;
                        break;
                    }
                }
                if (criticalStatements) {
                    break;
                }
            }

            ASTFinallyStatement finallyClause = t.getFinallyClause();
            if (t.getBeginLine() > id.getBeginLine() && finallyClause != null) {
                ASTBlock finallyBody = finallyClause.getBody();
                List<ASTName> names = finallyBody.findDescendantsOfType(ASTName.class);
                for (ASTName oName : names) {
                    String name = oName.getImage();
                    if (name != null && name.contains(".")) {
                        String[] parts = name.split("\\.");
                        if (parts.length == 2) {
                            String methodName = parts[1];
                            String varName = parts[0];
                            if (varName.equals(variableToClose) && closeTargets.contains(methodName)
                                    && nullCheckIfCondition(finallyBody, oName, varName)) {
                                closed = true;
                                break;
                            }

                        }
                    }
                }
                if (closed) {
                    break;
                }

                List<ASTStatementExpression> exprs = finallyBody.findDescendantsOfType(ASTStatementExpression.class, true);
                for (ASTStatementExpression stmt : exprs) {
                    ASTPrimaryExpression expr = stmt.getFirstChildOfType(ASTPrimaryExpression.class);
                    if (expr != null) {
                        ASTPrimaryPrefix prefix = expr.getFirstChildOfType(ASTPrimaryPrefix.class);
                        ASTPrimarySuffix suffix = expr.getFirstChildOfType(ASTPrimarySuffix.class);
                        if (prefix != null && suffix != null) {
                            if (prefix.getImage() == null) {
                                ASTName prefixName = prefix.getFirstChildOfType(ASTName.class);
                                if (prefixName != null && closeTargets.contains(prefixName.getImage())) {
                                    // Found a call to a "close target" that is
                                    // a direct
                                    // method call without a "ClassName."
                                    // prefix.
                                    closed = variableIsPassedToMethod(expr, variableToClose);
                                    if (closed) {
                                        break;
                                    }
                                }
                            } else if (suffix.getImage() != null) {
                                String prefixPlusSuffix = prefix.getImage() + "." + suffix.getImage();
                                if (closeTargets.contains(prefixPlusSuffix)) {
                                    // Found a call to a "close target" that is
                                    // a method call
                                    // in the form "ClassName.methodName".
                                    closed = variableIsPassedToMethod(expr, variableToClose);
                                    if (closed) {
                                        break;
                                    }
                                }
                            }
                            // look for primary suffix containing the close
                            // Targets elements.
                            // If the .close is executed in another class
                            // accessed by a method
                            // this form :
                            // getProviderInstance().closeConnexion(connexion)
                            // For this use case, we assume the variable is
                            // correctly closed
                            // in the other class since there is no way to
                            // really check it.
                            if (!closed) {
                                List<ASTPrimarySuffix> suffixes = expr.findDescendantsOfType(ASTPrimarySuffix.class, true);
                                for (ASTPrimarySuffix oSuffix : suffixes) {
                                    String suff = oSuffix.getImage();
                                    if (closeTargets.contains(suff)) {
                                        closed = variableIsPassedToMethod(expr, variableToClose);
                                        if (closed) {
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                if (closed) {
                    break;
                }
            } else if (t.isTryWithResources()) {
                // maybe the variable is used as a resource
                List<ASTName> names = t.getFirstChildOfType(ASTResourceSpecification.class).findDescendantsOfType(ASTName.class);
                for (ASTName potentialUsage : names) {
                    if (potentialUsage.hasImageEqualTo(variableToClose)) {
                        closed = true;
                        break;
                    }
                }
            }
        }

        if (!closed) {
            // See if the variable is returned by the method, which means the
            // method is a utility for creating the db resource, which means of
            // course it can't be closed by the method, so it isn't an error.
            List<ASTReturnStatement> returns = top.findDescendantsOfType(ASTReturnStatement.class, true);
            for (ASTReturnStatement returnStatement : returns) {
                ASTName name = returnStatement.getFirstDescendantOfType(ASTName.class);
                if (name != null && name.getImage().equals(variableToClose)) {
                    closed = true;
                    break;
                }
            }
        }

        // if all is not well, complain
        if (!closed) {
            ASTLocalVariableDeclaration localVarDecl = id.getFirstParentOfType(ASTLocalVariableDeclaration.class);
            Class<?> typeClass = type.getType();
            if (typeClass != null) {
                addViolation(data, id, typeClass.getSimpleName());
            } else if (localVarDecl != null && localVarDecl.getTypeNode() != null) {
                addViolation(data, id, localVarDecl.getTypeNode().getTypeImage());
            } else {
                addViolation(data, id, id.getVariableName());
            }
        }
    }

    private boolean variableIsPassedToMethod(ASTPrimaryExpression expr, String variable) {
        List<ASTName> methodParams = expr.findDescendantsOfType(ASTName.class, true);
        for (ASTName pName : methodParams) {
            String paramName = pName.getImage();
            // also check if we've got the a parameter (i.e if it's an argument
            // !)
            ASTArgumentList parentParam = pName.getFirstParentOfType(ASTArgumentList.class);
            if (paramName.equals(variable) && parentParam != null) {
                return true;
            }
        }
        return false;
    }

    private ASTIfStatement findIfStatement(ASTBlock enclosingBlock, Node node) {
        ASTIfStatement ifStatement = node.getFirstParentOfType(ASTIfStatement.class);
        List<ASTIfStatement> allIfStatements = enclosingBlock.findDescendantsOfType(ASTIfStatement.class);
        if (ifStatement != null && allIfStatements.contains(ifStatement)) {
            return ifStatement;
        }
        return null;
    }

    /**
     * Checks, whether the given node is inside a if condition, and if so,
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
    private boolean nullCheckIfCondition(ASTBlock enclosingBlock, Node node, String varName) {
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
}
