/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * Flags identical catch branches, which can be collapsed into a multi-catch.
 *
 * @author Clément Fournier
 * @since 6.2.0
 */
public class IdenticalCatchBranchesRule extends AbstractJavaRule {


    private boolean areEquivalent(ASTCatchStatement st1, ASTCatchStatement st2) {
        return tokenEquals(st1.getBlock(), st2.getBlock(), st1.getExceptionName(), st2.getExceptionName());
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


    // Would be cleaner with java8 streams. Maybe this can be moved to AbstractJavaNode then
    private static Iterator<String> tokenImageIterator(final AbstractJavaNode node) {
        return new Iterator<String>() {

            GenericToken current = node.jjtGetFirstToken();


            @Override
            public boolean hasNext() {
                // the first
                return current != null && current != node.jjtGetLastToken().getNext();
            }


            @Override
            public String next() {
                GenericToken token = current;
                current = current.getNext();
                return token.getImage();
            }


            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    /**
     * Returns true if the image of the tokens of this node are
     * equal to the image of the tokens of the other node. This
     * method implements one definition of equality between two
     * nodes, based solely on the equality of their string
     * representation in the source.
     *
     * @param node First node
     * @param that Second node
     *
     * @return Whether this node is equal to that node, or not
     */
    private static boolean tokenEquals(AbstractJavaNode node, AbstractJavaNode that, String nodeName, String thatName) {
        if (that == null) {
            return false;
        } else {
            Iterator<String> thisIt = tokenImageIterator(node);
            Iterator<String> thatIt = tokenImageIterator(that);
            while (thisIt.hasNext()) {
                if (!thatIt.hasNext()) {
                    return false;
                }
                String o1 = thisIt.next();
                String o2 = thatIt.next();
                if (!Objects.equals(o1, o2)
                    // wouldn't be sensitive to changes in the exception variable name
                    && !(Objects.equals(o1, nodeName) && Objects.equals(o2, thatName))) {
                    return false;
                }
            }
            return !thatIt.hasNext();
        }
    }


}
