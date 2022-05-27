/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

/**
 * This rule finds places where StringBuffer.toString() is called just to see if
 * the string is 0 length by either using .equals("") or toString().length().
 *
 * <pre>
 * StringBuffer sb = new StringBuffer(&quot;some string&quot;);
 * if (sb.toString().equals(&quot;&quot;)) {
 *     // this is wrong
 * }
 * if (sb.length() == 0) {
 *     // this is right
 * }
 * </pre>
 *
 * @author acaplan
 * @author Philip Graf
 */
public class UseStringBufferLengthRule extends AbstractJavaRule {

    // FIXME Need to remove this somehow.
    /*
     * Specifically, we need a symbol tree that can be traversed downwards, so
     * that instead of visiting each name and then visiting the declaration for
     * that name, we should visit all the declarations and check their usages.
     * With that in place, this rule would be reduced to: - find all
     * StringBuffer declarations - check each usage - flag those that involve
     * variable.toString()
     */
    private Set<NameDeclaration> alreadySeen = new HashSet<>();

    @Override
    public Object visit(ASTMethodDeclaration acu, Object data) {
        alreadySeen.clear();
        return super.visit(acu, data);
    }

    @Override
    public Object visit(ASTName decl, Object data) {
        if (!decl.getImage().endsWith("toString")) {
            return super.visit(decl, data);
        }
        NameDeclaration nd = decl.getNameDeclaration();
        if (nd == null) {
            return super.visit(decl, data);
        }
        if (alreadySeen.contains(nd) || !(nd instanceof VariableNameDeclaration)
                || !ConsecutiveLiteralAppendsRule.isStringBuilderOrBuffer(((VariableNameDeclaration) nd).getDeclaratorId())) {
            return super.visit(decl, data);
        }
        alreadySeen.add(nd);

        if (isViolation(decl)) {
            addViolation(data, decl);
        }

        return super.visit(decl, data);
    }

    /**
     * Returns true for the following violations:
     *
     * <pre>
     * StringBuffer sb = new StringBuffer(&quot;some string&quot;);
     * if (sb.toString().equals(&quot;&quot;)) {
     *     // this is a violation
     * }
     * if (sb.toString().length() == 0) {
     *     // this is a violation
     * }
     * if (sb.length() == 0) {
     *     // this is ok
     * }
     * </pre>
     */
    private boolean isViolation(ASTName decl) {
        // the (grand)parent of a violation has four children
        Node parent = decl.getParent().getParent();
        if (parent.getNumChildren() == 4) {
            // 1. child: sb.toString where sb is a VariableNameDeclaration for a
            // StringBuffer or StringBuilder
            if (parent.getChild(0).getFirstChildOfType(ASTName.class).getImage().endsWith(".toString")) {
                // 2. child: the arguments of toString
                // no need to check as both StringBuffer and StringBuilder only
                // have one toString method
                // 3. child: equals or length, 4. child: their arguments
                return isEqualsViolation(parent) || isLengthViolation(parent);
            }
        }
        return false;
    }

    private boolean isEqualsViolation(Node parent) {
        // 3. child: equals
        if (parent.getChild(2).hasImageEqualTo("equals")) {
            // 4. child: the arguments of equals, there must be exactly one and
            // it must be "" or a final variable initialized with ""
            Node primarySuffix = parent.getChild(3);
            List<ASTArgumentList> methodCalls = primarySuffix.findDescendantsOfType(ASTArgumentList.class);
            if (methodCalls.size() == 1 && methodCalls.get(0).size() == 1) {
                ASTExpression firstArgument = primarySuffix.getChild(0).getFirstChildOfType(ASTArgumentList.class)
                        .findChildrenOfType(ASTExpression.class).get(0);
                List<ASTLiteral> literals = firstArgument.findDescendantsOfType(ASTLiteral.class);
                if (literals.isEmpty()) {
                    literals = findLiteralsInVariableInitializer(firstArgument);
                }
                return literals.size() == 1 && literals.get(0).hasImageEqualTo("\"\"");
            }
        }
        return false;
    }

    private List<ASTLiteral> findLiteralsInVariableInitializer(ASTExpression firstArgument) {
        List<ASTName> varAccess = firstArgument.findDescendantsOfType(ASTName.class);
        if (varAccess.size() == 1) {
            NameDeclaration nameDeclaration = varAccess.get(0).getNameDeclaration();
            if (nameDeclaration != null && nameDeclaration.getNode() instanceof ASTVariableDeclaratorId) {
                ASTVariableDeclaratorId varId = (ASTVariableDeclaratorId) nameDeclaration.getNode();
                if (varId.isFinal() && varId.getParent() instanceof ASTVariableDeclarator) {
                    ASTVariableDeclarator declarator = (ASTVariableDeclarator) varId.getParent();
                    if (declarator.getInitializer() != null) {
                        return declarator.getInitializer().findDescendantsOfType(ASTLiteral.class);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean isLengthViolation(Node parent) {
        // 3. child: length
        return parent.getChild(2).hasImageEqualTo("length");
        // 4. child: the arguments of length
        // no need to check as String has only one length method
    }

}
