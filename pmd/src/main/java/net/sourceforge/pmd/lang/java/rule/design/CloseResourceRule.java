/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
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
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

import org.jaxen.JaxenException;

/**
 * Makes sure you close your database connections. It does this by
 * looking for code patterned like this:
 * <pre>
 *  Connection c = X;
 *  try {
 *   // do stuff, and maybe catch something
 *  } finally {
 *   c.close();
 *  }
 *
 *  @author original author unknown
 *  @author Contribution from Pierre Mathien
 * </pre>
 */
public class CloseResourceRule extends AbstractJavaRule {

    private Set<String> types = new HashSet<String>();
    private Set<String> simpleTypes = new HashSet<String>();

    private Set<String> closeTargets = new HashSet<String>();
    private static final StringMultiProperty CLOSE_TARGETS_DESCRIPTOR = new StringMultiProperty("closeTargets",
            "Methods which may close this resource", new String[]{}, 1.0f, ',');

    private static final StringMultiProperty TYPES_DESCRIPTOR = new StringMultiProperty("types",
            "Affected types", new String[]{"java.sql.Connection","java.sql.Statement","java.sql.ResultSet"}, 2.0f, ',');
    
    public CloseResourceRule() {
	definePropertyDescriptor(CLOSE_TARGETS_DESCRIPTOR);
	definePropertyDescriptor(TYPES_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        if (closeTargets.isEmpty() && getProperty(CLOSE_TARGETS_DESCRIPTOR) != null) {
            closeTargets.addAll(Arrays.asList(getProperty(CLOSE_TARGETS_DESCRIPTOR)));
        }
        if (types.isEmpty() && getProperty(TYPES_DESCRIPTOR) != null) {
            types.addAll(Arrays.asList(getProperty(TYPES_DESCRIPTOR)));
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
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        checkForResources(node, data);
        return data;
    }

    private void checkForResources(Node node, Object data) {
        List<ASTLocalVariableDeclaration> vars = node.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        List<ASTVariableDeclaratorId> ids = new ArrayList<ASTVariableDeclaratorId>();

        // find all variable references to Connection objects
        for (ASTLocalVariableDeclaration var: vars) {
            ASTType type = var.getTypeNode();

            if (type.jjtGetChild(0) instanceof ASTReferenceType) {
                ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
                if (ref.jjtGetChild(0) instanceof ASTClassOrInterfaceType) {
                    ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);

                    if (clazz.getType() != null && types.contains(clazz.getType().getName())
                        || (clazz.getType() == null && simpleTypes.contains(toSimpleType(clazz.getImage())))
                        || types.contains(clazz.getImage())) {

                        // if the variables are initialized with null, then they are ignored.
                        // At some point later in the code, there is an assignment - however, this is currently ignored
                        if (!hasNullInitializer(var)) {
                            ASTVariableDeclaratorId id = var.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
                            ids.add(id);
                        }
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
                List<?> nulls = init.findChildNodesWithXPath("Expression/PrimaryExpression/PrimaryPrefix/Literal/NullLiteral");
                return !nulls.isEmpty();
            } catch (JaxenException e) {
                return false;
            }
        }
        return false;
    }

    private void ensureClosed(ASTLocalVariableDeclaration var,
                              ASTVariableDeclaratorId id, Object data) {
        // What are the chances of a Connection being instantiated in a
        // for-loop init block? Anyway, I'm lazy!
        String variableToClose = id.getImage();
        String target = variableToClose + ".close";
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

            // verifies that there are no critical statements between the variable declaration and
            // the beginning of the try block.
            ASTBlockStatement tryBlock = t.getFirstParentOfType(ASTBlockStatement.class);
            if (parentBlock.jjtGetParent() == tryBlock.jjtGetParent()) {

                List<ASTBlockStatement> blocks = parentBlock.jjtGetParent().findChildrenOfType(ASTBlockStatement.class);
                int parentBlockIndex = blocks.indexOf(parentBlock);
                int tryBlockIndex = blocks.indexOf(tryBlock);
                boolean criticalStatements = false;

                for (int i = parentBlockIndex + 1; i < tryBlockIndex; i++) {
                    // assume variable declarations are not critical
                    ASTLocalVariableDeclaration varDecl = blocks.get(i).getFirstDescendantOfType(ASTLocalVariableDeclaration.class);
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
                    if (name.equals(target)) {
                        closed = true;
                        break;
                    }
					if (name.contains(".")) {
						String[] parts = name.split("\\.");
						if (parts.length == 2) {
							String methodName = parts[1];
							String varName = parts[0];
							if (varName.equals(variableToClose)
									&& closeTargets.contains(methodName)) {
								closed = true;
								break;
							}

						}
					}
                }
                if (closed) {
                    break;
                }

                List<ASTStatementExpression> exprs = new ArrayList<ASTStatementExpression>();
                f.findDescendantsOfType(ASTStatementExpression.class, exprs, true);
                for (ASTStatementExpression stmt : exprs) {
                    ASTPrimaryExpression expr =
                        stmt.getFirstChildOfType(ASTPrimaryExpression.class);
                    if (expr != null) {
                        ASTPrimaryPrefix prefix = expr.getFirstChildOfType(ASTPrimaryPrefix.class);
                        ASTPrimarySuffix suffix = expr.getFirstChildOfType(ASTPrimarySuffix.class);
                        if ((prefix != null) && (suffix != null)) {
                            if (prefix.getImage() == null) {
                                ASTName prefixName = prefix.getFirstChildOfType(ASTName.class);
                                if ((prefixName != null)
                                        && closeTargets.contains(prefixName.getImage()))
                                {
                                    // Found a call to a "close target" that is a direct
                                    // method call without a "ClassName." prefix.
                                    closed = variableIsPassedToMethod(expr, variableToClose);
                                    if (closed) {
                                        break;
                                    }
                                }
                            } else if (suffix.getImage() != null) {
                                String prefixPlusSuffix =
                                        prefix.getImage()+ "." + suffix.getImage();
                                if (closeTargets.contains(prefixPlusSuffix)) {
                                    // Found a call to a "close target" that is a method call
                                    // in the form "ClassName.methodName".
                                    closed = variableIsPassedToMethod(expr, variableToClose);
                                    if (closed) {
                                        break;
                                    }
                                }
                            }
                         // look for primary suffix containing the close Targets elements.
                            // If the .close is executed in another class accessed by a method
                            // this form : getProviderInstance().closeConnexion(connexion)
                            // For this use case, we assume the variable is correctly closed
                            // in the other class since there is no way to really check it.
                            if (!closed)
                            {
                                List<ASTPrimarySuffix> suffixes = new ArrayList<ASTPrimarySuffix>();
                                expr.findDescendantsOfType(ASTPrimarySuffix.class, suffixes, true);
                                for (ASTPrimarySuffix oSuffix : suffixes) {
                                    String suff = oSuffix.getImage();
                                    if (closeTargets.contains(suff)) 
                                    {
                                        closed = variableIsPassedToMethod(expr, variableToClose);
                                        if(closed)
                                        { 
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
            List<ASTReturnStatement> returns = new ArrayList<ASTReturnStatement>();
            top.findDescendantsOfType(ASTReturnStatement.class, returns, true);
            for (ASTReturnStatement returnStatement : returns) {
                ASTName name = returnStatement.getFirstDescendantOfType(ASTName.class);
                if ((name != null) && name.getImage().equals(variableToClose)) {
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
        List<ASTName> methodParams = new ArrayList<ASTName>();
        expr.findDescendantsOfType(ASTName.class, methodParams, true);
        for (ASTName pName : methodParams) {
            String paramName = pName.getImage();
            // also check if we've got the a parameter (i.e if it's an argument !) 
            ASTArgumentList parentParam = pName.getFirstParentOfType(ASTArgumentList.class);
            if (paramName.equals(variable) && parentParam != null) {
                return true;
            }
        }
        return false;
    }
}