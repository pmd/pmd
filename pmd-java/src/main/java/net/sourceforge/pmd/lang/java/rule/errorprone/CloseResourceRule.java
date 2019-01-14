/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
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
                    .defaultValues("java.sql.Connection", "java.sql.Statement", "java.sql.ResultSet")
                    .delim(',').build();

    private static final PropertyDescriptor<Boolean> USE_CLOSE_AS_DEFAULT_TARGET =
            booleanProperty("closeAsDefaultTarget")
                    .desc("Consider 'close' as a target by default").defaultValue(true).build();


    public CloseResourceRule() {
        definePropertyDescriptor(CLOSE_TARGETS_DESCRIPTOR);
        definePropertyDescriptor(TYPES_DESCRIPTOR);
        definePropertyDescriptor(USE_CLOSE_AS_DEFAULT_TARGET);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        if (closeTargets.isEmpty() && getProperty(CLOSE_TARGETS_DESCRIPTOR) != null) {
            closeTargets.addAll(getProperty(CLOSE_TARGETS_DESCRIPTOR));
        }
        if (getProperty(USE_CLOSE_AS_DEFAULT_TARGET) && !closeTargets.contains("close")) {
            closeTargets.add("close");
        }
        if (types.isEmpty() && getProperty(TYPES_DESCRIPTOR) != null) {
            types.addAll(getProperty(TYPES_DESCRIPTOR));
        }
        if (simpleTypes.isEmpty() && getProperty(TYPES_DESCRIPTOR) != null) {
            for (String type : getProperty(TYPES_DESCRIPTOR)) {
                simpleTypes.add(toSimpleType(type));
            }
        }
        return super.visit(node, data);
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

    private void checkForResources(Node node, Object data) {
        List<ASTLocalVariableDeclaration> vars = node.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        List<ASTVariableDeclaratorId> ids = new ArrayList<>();

        // find all variable references to Connection objects
        for (ASTLocalVariableDeclaration var : vars) {
            ASTType type = var.getTypeNode();

            if (type != null && type.jjtGetChild(0) instanceof ASTReferenceType) {
                ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
                if (ref.jjtGetChild(0) instanceof ASTClassOrInterfaceType) {
                    ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);

                    if (clazz.getType() != null && types.contains(clazz.getType().getName())
                            || clazz.getType() == null && simpleTypes.contains(toSimpleType(clazz.getImage()))
                                    && !clazz.isReferenceToClassSameCompilationUnit()
                            || types.contains(clazz.getImage()) && !clazz.isReferenceToClassSameCompilationUnit()) {

                        ASTVariableDeclaratorId id = var.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
                        ids.add(id);
                    }
                }
            }
        }

        // if there are connections, ensure each is closed.
        for (ASTVariableDeclaratorId x : ids) {
            ensureClosed((ASTLocalVariableDeclaration) x.jjtGetParent().jjtGetParent(), x, data);
        }
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

    private void ensureClosed(ASTLocalVariableDeclaration var, ASTVariableDeclaratorId id, Object data) {
        // What are the chances of a Connection being instantiated in a
        // for-loop init block? Anyway, I'm lazy!
        String variableToClose = id.getImage();
        Node n = var;

        while (!(n instanceof ASTBlock) && !(n instanceof ASTConstructorDeclaration)) {
            n = n.jjtGetParent();
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
            if (!hasNullInitializer(var) && parentBlock.jjtGetParent() == tryBlock.jjtGetParent()) {

                List<ASTBlockStatement> blocks = parentBlock.jjtGetParent().findChildrenOfType(ASTBlockStatement.class);
                int parentBlockIndex = blocks.indexOf(parentBlock);
                int tryBlockIndex = blocks.indexOf(tryBlock);
                boolean criticalStatements = false;

                for (int i = parentBlockIndex + 1; i < tryBlockIndex; i++) {
                    // assume variable declarations are not critical
                    ASTLocalVariableDeclaration varDecl = blocks.get(i)
                            .getFirstDescendantOfType(ASTLocalVariableDeclaration.class);
                    if (varDecl == null) {
                        criticalStatements = true;
                        break;
                    }
                }
                if (criticalStatements) {
                    break;
                }
            }

            if (t.getBeginLine() > id.getBeginLine() && t.hasFinally()) {
                ASTBlock f = (ASTBlock) t.getFinally().jjtGetChild(0);
                List<ASTName> names = f.findDescendantsOfType(ASTName.class);
                for (ASTName oName : names) {
                    String name = oName.getImage();
                    if (name != null && name.contains(".")) {
                        String[] parts = name.split("\\.");
                        if (parts.length == 2) {
                            String methodName = parts[1];
                            String varName = parts[0];
                            if (varName.equals(variableToClose) && closeTargets.contains(methodName)
                                    && nullCheckIfCondition(f, oName, varName)) {
                                closed = true;
                                break;
                            }

                        }
                    }
                }
                if (closed) {
                    break;
                }

                List<ASTStatementExpression> exprs = new ArrayList<>();
                f.findDescendantsOfType(ASTStatementExpression.class, exprs, true);
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
                                List<ASTPrimarySuffix> suffixes = new ArrayList<>();
                                expr.findDescendantsOfType(ASTPrimarySuffix.class, suffixes, true);
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
            }
        }

        if (!closed) {
            // See if the variable is returned by the method, which means the
            // method is a utility for creating the db resource, which means of
            // course it can't be closed by the method, so it isn't an error.
            List<ASTReturnStatement> returns = new ArrayList<>();
            top.findDescendantsOfType(ASTReturnStatement.class, returns, true);
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
            ASTType type = var.getFirstChildOfType(ASTType.class);
            ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
            ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);
            addViolation(data, id, clazz.getImage());
        }
    }

    private boolean variableIsPassedToMethod(ASTPrimaryExpression expr, String variable) {
        List<ASTName> methodParams = new ArrayList<>();
        expr.findDescendantsOfType(ASTName.class, methodParams, true);
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
