/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * Flags identical catch branches, which can be collapsed into a multi-catch.
 *
 * @author Clément Fournier
 * @since 6.4.0
 */
public class IdenticalCatchBranchesRule extends AbstractJavaRule {


    private boolean areEquivalent(ASTCatchStatement st1, ASTCatchStatement st2) {
        return hasSameSubTree(st1.getBlock(), st2.getBlock(), st1.getExceptionName(), st2.getExceptionName());
    }


    // groups catch statements by equivalence class, according to the string value of their tokens.
    private Set<List<ASTCatchStatement>> equivalenceClasses(List<ASTCatchStatement> catches) {
        Set<List<ASTCatchStatement>> result = new HashSet<>(catches.size());
        for (ASTCatchStatement stmt : catches) {
            if (result.isEmpty()) {
                result.add(newEquivClass(stmt));
                continue;
            }

            boolean isNewClass = true;
            for (List<ASTCatchStatement> equivClass : result) {
                if (areEquivalent(stmt, equivClass.get(0))) {
                    equivClass.add(stmt);
                    isNewClass = false;
                    break;
                }
            }

            if (isNewClass) {
                result.add(newEquivClass(stmt));
            }
        }

        return result;
    }


    private List<ASTCatchStatement> newEquivClass(ASTCatchStatement stmt) {
        // Each equivalence class is sorted by document order
        List<ASTCatchStatement> result = new ArrayList<>(2);
        result.add(stmt);
        return result;
    }


    // Gets the representation of the set of catch statements as a single multicatch
    private String getCaughtExceptionsAsString(ASTCatchStatement stmt) {

        StringBuilder sb = new StringBuilder();

        final String delim = " | ";
        for (ASTType type : stmt.getCaughtExceptionTypeNodes()) {
            sb.append(type.getTypeImage()).append(delim);
        }

        // remove the last delimiter
        sb.replace(sb.length() - 3, sb.length(), "");
        return sb.toString();
    }


    @Override
    public Object visit(ASTTryStatement node, Object data) {

        List<ASTCatchStatement> catchStatements = node.getCatchStatements();
        Set<List<ASTCatchStatement>> equivClasses = equivalenceClasses(catchStatements);

        for (List<ASTCatchStatement> identicalStmts : equivClasses) {
            if (identicalStmts.size() > 1) {
                String identicalBranchName = getCaughtExceptionsAsString(identicalStmts.get(0));

                // By convention, lower catch blocks are collapsed into the highest one
                // The first node of the equivalence class is thus the block that should be transformed
                for (int i = 1; i < identicalStmts.size(); i++) {
                    addViolation(data, identicalStmts.get(i), new String[]{identicalBranchName, });
                }
            }
        }

        return super.visit(node, data);
    }

    /**
     * Checks whether two nodes has same subtree.
     * Note, it wouldn't be sensitive to changes in the exception variable name.
     *
     * @param node1
     *             the first node to check
     * @param node2
     *             the second node to check
     * @param exceptionName1
     *             the first exception variable name
     * @param exceptionName2
     *             the second exception variable name
     * @return <code>ture</code> if two nodes has same subtree, otherwise <code>false</code>
     */
    private boolean hasSameSubTree(Node node1, Node node2, String exceptionName1, String exceptionName2) {
        if (node1 == null && node2 == null) {
            return true;
        } else if (node1 == null || node2 == null) {
            return false;
        }

        //numbers of child node are different
        if (node1.jjtGetNumChildren() != node2.jjtGetNumChildren()) {
            return false;
        }

        for (int num = 0; num < node1.jjtGetNumChildren(); num++) {

            //type of nodes are different
            if (node1.jjtGetChild(num).getClass() != node2.jjtGetChild(num).getClass()) {
                return false;
            }

            String image1 = node1.jjtGetChild(num).getImage();
            String image2 = node2.jjtGetChild(num).getImage();

            //image of nodes are different
            if (!Objects.equals(image1, image2)
                // wouldn't be sensitive to changes in the exception variable name
                && !Objects.equals(image1, exceptionName1) && Objects.equals(image2, exceptionName2)) {
                return false;
            }

            //subtree of nodes are different
            if (!hasSameSubTree(node1.jjtGetChild(num), node2.jjtGetChild(num),
                exceptionName1, exceptionName2)) {
                return false;
            }
        }
        return true;
    }
}
