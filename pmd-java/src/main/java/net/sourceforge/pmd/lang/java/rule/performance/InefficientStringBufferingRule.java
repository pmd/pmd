/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * How this rule works: find additive expressions: + check that the addition is
 * between anything other than two literals if true and also the parent is
 * StringBuffer constructor or append, report a violation.
 *
 * @author mgriffa
 */
public class InefficientStringBufferingRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        ASTBlockStatement bs = node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        int immediateLiterals = 0;
        int immediateStringLiterals = 0;
        List<ASTLiteral> nodes = node.findDescendantsOfType(ASTLiteral.class);
        for (ASTLiteral literal : nodes) {
            if (literal.getNthParent(3) instanceof ASTAdditiveExpression) {
                immediateLiterals++;
                if (literal.isStringLiteral()) {
                    immediateStringLiterals++;
                }
            }
            if (literal.isIntLiteral() || literal.isFloatLiteral() || literal.isDoubleLiteral()
                    || literal.isLongLiteral()) {
                return data;
            }
        }

        if (immediateLiterals > 1) {
            return data;
        }

        // if literal + public static final, return
        List<ASTName> nameNodes = node.findDescendantsOfType(ASTName.class);
        for (ASTName name : nameNodes) {
            if (name.getNameDeclaration() != null && name.getNameDeclaration() instanceof VariableNameDeclaration) {
                VariableNameDeclaration vnd = (VariableNameDeclaration) name.getNameDeclaration();
                AccessNode accessNodeParent = vnd.getAccessNodeParent();
                if (accessNodeParent.isFinal() && accessNodeParent.isStatic()) {
                    return data;
                }
            }
        }

        // if literal primitive type and not strings variables, then return
        boolean stringFound = false;
        for (ASTName name : nameNodes) {
            if (!isPrimitiveType(name) && isStringType(name)) {
                stringFound = true;
                break;
            }
        }
        if (!stringFound && immediateStringLiterals == 0) {
            return data;
        }

        if (bs.isAllocation()) {
            for (Iterator<ASTName> iterator = nameNodes.iterator(); iterator.hasNext();) {
                ASTName name = iterator.next();
                if (!name.getImage().endsWith("length")) {
                    break;
                } else if (!iterator.hasNext()) {
                    return data; // All names end with length
                }
            }

            if (isAllocatedStringBuffer(node)) {
                addViolation(data, node);
            }
        } else if (isInStringBufferOperation(node, 6, "append")) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean isStringType(ASTName name) {
        ASTType type = getTypeNode(name);
        if (type != null) {
            List<ASTClassOrInterfaceType> types = type.findDescendantsOfType(ASTClassOrInterfaceType.class);
            if (!types.isEmpty()) {
                ASTClassOrInterfaceType typeDeclaration = types.get(0);
                if (String.class == typeDeclaration.getType() || "String".equals(typeDeclaration.getImage())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPrimitiveType(ASTName name) {
        ASTType type = getTypeNode(name);
        return type != null && !type.findChildrenOfType(ASTPrimitiveType.class).isEmpty();
    }

    private ASTType getTypeNode(ASTName name) {
        if (name.getNameDeclaration() instanceof VariableNameDeclaration) {
            VariableNameDeclaration vnd = (VariableNameDeclaration) name.getNameDeclaration();
            if (vnd.getAccessNodeParent() instanceof ASTLocalVariableDeclaration) {
                ASTLocalVariableDeclaration l = (ASTLocalVariableDeclaration) vnd.getAccessNodeParent();
                return l.getTypeNode();
            } else if (vnd.getAccessNodeParent() instanceof ASTFormalParameter) {
                ASTFormalParameter p = (ASTFormalParameter) vnd.getAccessNodeParent();
                return p.getTypeNode();
            }
        }
        return null;
    }

    protected static boolean isInStringBufferOperation(Node node, int length, String methodName) {
        if (!(node.getNthParent(length) instanceof ASTStatementExpression)) {
            return false;
        }
        ASTStatementExpression s = node.getFirstParentOfType(ASTStatementExpression.class);
        if (s == null) {
            return false;
        }
        ASTName n = s.getFirstDescendantOfType(ASTName.class);
        if (n == null || n.getImage().indexOf(methodName) == -1
                || !(n.getNameDeclaration() instanceof TypedNameDeclaration)) {
            return false;
        }

        // TODO having to hand-code this kind of dredging around is ridiculous
        // we need something to support this in the framework
        // but, "for now" (tm):
        // if more than one arg to append(), skip it
        ASTArgumentList argList = s.getFirstDescendantOfType(ASTArgumentList.class);
        if (argList == null || argList.getNumChildren() > 1) {
            return false;
        }
        return TypeHelper.isExactlyAny((TypedNameDeclaration) n.getNameDeclaration(), StringBuffer.class,
                StringBuilder.class);
    }

    private boolean isAllocatedStringBuffer(ASTAdditiveExpression node) {
        ASTAllocationExpression ao = node.getFirstParentOfType(ASTAllocationExpression.class);
        if (ao == null) {
            return false;
        }
        // note that the child can be an ArrayDimsAndInits, for example, from
        // java.lang.FloatingDecimal: t = new int[ nWords+wordcount+1 ];
        ASTClassOrInterfaceType an = ao.getFirstChildOfType(ASTClassOrInterfaceType.class);
        return an != null && TypeHelper.isEither(an, StringBuffer.class, StringBuilder.class);
    }
}
