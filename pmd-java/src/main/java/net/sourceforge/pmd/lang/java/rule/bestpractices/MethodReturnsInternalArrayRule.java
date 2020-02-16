/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;

/**
 * Implementation note: this rule currently ignores return types of y.x.z,
 * currently it handles only local type fields. Created on Jan 17, 2005
 *
 * @author mgriffa
 */
public class MethodReturnsInternalArrayRule extends AbstractSunSecureRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (!method.getResultType().returnsArray() || method.isPrivate()) {
            return data;
        }
        List<ASTReturnStatement> returns = method.findDescendantsOfType(ASTReturnStatement.class);
        ASTAnyTypeDeclaration td = method.getFirstParentOfType(ASTAnyTypeDeclaration.class);
        for (ASTReturnStatement ret : returns) {
            final String vn = getReturnedVariableName(ret);
            if (!isField(vn, td)) {
                continue;
            }
            if (ret.findDescendantsOfType(ASTPrimarySuffix.class).size() > 2) {
                continue;
            }
            if (ret.hasDescendantOfType(ASTAllocationExpression.class)) {
                continue;
            }
            if (hasArraysCopyOf(ret)) {
                continue;
            }
            if (hasClone(ret, vn)) {
                continue;
            }
            if (isEmptyArray(vn, td)) {
                continue;
            }
            if (!isLocalVariable(vn, method)) {
                addViolation(data, ret, vn);
            } else {
                // This is to handle field hiding
                final ASTPrimaryPrefix pp = ret.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                if (pp != null && pp.usesThisModifier()) {
                    final ASTPrimarySuffix ps = ret.getFirstDescendantOfType(ASTPrimarySuffix.class);
                    if (ps.hasImageEqualTo(vn)) {
                        addViolation(data, ret, vn);
                    }
                }
            }
        }
        return data;
    }

    private boolean hasClone(ASTReturnStatement ret, String varName) {
        List<ASTPrimaryExpression> expressions = ret.findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression e : expressions) {
            if (e.getChild(0) instanceof ASTPrimaryPrefix && e.getNumChildren() == 2
                    && e.getChild(1) instanceof ASTPrimarySuffix
                    && ((ASTPrimarySuffix) e.getChild(1)).isArguments()
                    && ((ASTPrimarySuffix) e.getChild(1)).getArgumentCount() == 0) {
                ASTName name = e.getFirstDescendantOfType(ASTName.class);
                if (name != null && name.hasImageEqualTo(varName + ".clone")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasArraysCopyOf(ASTReturnStatement ret) {
        List<ASTPrimaryExpression> expressions = ret.findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression e : expressions) {
            if (e.getNumChildren() == 2 && e.getChild(0) instanceof ASTPrimaryPrefix
                    && e.getChild(0).getNumChildren() == 1 && e.getChild(0).getChild(0) instanceof ASTName
                    && e.getChild(0).getChild(0).getImage().endsWith("Arrays.copyOf")) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptyArray(String varName, ASTAnyTypeDeclaration typeDeclaration) {
        final List<ASTFieldDeclaration> fds = typeDeclaration.findDescendantsOfType(ASTFieldDeclaration.class);
        if (fds != null) {
            for (ASTFieldDeclaration fd : fds) {
                final ASTVariableDeclaratorId vid = fd.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
                if (vid != null && vid.hasImageEqualTo(varName)) {
                    ASTVariableInitializer initializer = fd.getFirstDescendantOfType(ASTVariableInitializer.class);
                    if (initializer != null && initializer.getNumChildren() == 1) {
                        Node child = initializer.getChild(0);
                        if (child instanceof ASTArrayInitializer && child.getNumChildren() == 0) {
                            return true;
                        } else if (child instanceof ASTExpression) {
                            try {
                                List<? extends Node> arrayAllocation = child.findChildNodesWithXPath(
                                        "./PrimaryExpression/PrimaryPrefix/AllocationExpression/ArrayDimsAndInits/Expression/PrimaryExpression/PrimaryPrefix/Literal[@IntLiteral=\"true\"][@Image=\"0\"]");
                                if (arrayAllocation != null && arrayAllocation.size() == 1) {
                                    return true;
                                }
                            } catch (JaxenException e) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
