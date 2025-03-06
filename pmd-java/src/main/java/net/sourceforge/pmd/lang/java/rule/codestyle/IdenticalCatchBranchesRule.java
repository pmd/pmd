/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.util.OptionalBool;


/**
 * Flags identical catch branches, which can be collapsed into a multi-catch.
 *
 * @author Clément Fournier
 * @since 6.4.0
 */
public class IdenticalCatchBranchesRule extends AbstractJavaRulechainRule {

    public IdenticalCatchBranchesRule() {
        super(ASTTryStatement.class);
    }

    interface PartialEquivalenceRel<T> {
        OptionalBool test(T t1, T t2);
    }

    private boolean areEquivalent(ASTCatchClause st1, ASTCatchClause st2) {
        String e1Name = st1.getParameter().getName();
        String e2Name = st2.getParameter().getName();

        return JavaAstUtils.tokenEquals(st1.getBody(), st2.getBody(), name -> name.equals(e1Name) ? e2Name : name)
            && areStructurallyEquivalent(st1.getBody(), st2.getBody(),
                                         (n1, n2) -> {
                                             if (n1 instanceof InvocationNode) {
                                                 JExecutableSymbol sym1 = ((InvocationNode) n1).getMethodType().getSymbol();
                                                 JExecutableSymbol sym2 = ((InvocationNode) n2).getMethodType().getSymbol();
                                                 if (!Objects.equals(sym1, sym2)) {
                                                     return OptionalBool.NO;
                                                 }
                                             }
                                             return OptionalBool.UNKNOWN;
                                         });
    }

    /**
     * Check that both nodes have the same structure and that some semantic properties
     * of the trees match (eg, methods that are being called). This is not a full
     * equality routine, for instance we do not
     */
    private static boolean areStructurallyEquivalent(JavaNode n1, JavaNode n2, PartialEquivalenceRel<JavaNode> areEquivalent) {
        if (n1.getNumChildren() != n2.getNumChildren()
            || !n1.getClass().equals(n2.getClass())
            || areEquivalent.test(n1, n2) == OptionalBool.NO) {
            return false;
        }

        for (int i = 0; i < n1.getNumChildren(); i++) {
            if (!areStructurallyEquivalent(n1.getChild(i), n2.getChild(i), areEquivalent)) {
                return false;
            }
        }
        return true;
    }


    /** groups catch statements by equivalence class, according to the equivalence {@link #areEquivalent(ASTCatchClause, ASTCatchClause)}. */
    private Set<List<ASTCatchClause>> equivalenceClasses(List<ASTCatchClause> catches) {
        Set<List<ASTCatchClause>> result = new HashSet<>(catches.size());
        for (ASTCatchClause stmt : catches) {
            if (result.isEmpty()) {
                result.add(newEquivClass(stmt));
                continue;
            }

            boolean isNewClass = true;
            for (List<ASTCatchClause> equivClass : result) {
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


    private List<ASTCatchClause> newEquivClass(ASTCatchClause stmt) {
        // Each equivalence class is sorted by document order
        List<ASTCatchClause> result = new ArrayList<>(2);
        result.add(stmt);
        return result;
    }


    // Gets the representation of the set of catch statements as a single multicatch
    private String getCaughtExceptionsAsString(ASTCatchClause stmt) {
        return PrettyPrintingUtil.prettyPrintType(stmt.getParameter().getTypeNode());
    }


    @Override
    public Object visit(ASTTryStatement node, Object data) {

        List<ASTCatchClause> catchStatements = node.getCatchClauses().toList();
        Set<List<ASTCatchClause>> equivClasses = equivalenceClasses(catchStatements);

        for (List<ASTCatchClause> identicalStmts : equivClasses) {
            if (identicalStmts.size() > 1) {
                String identicalBranchName = getCaughtExceptionsAsString(identicalStmts.get(0));

                // By convention, lower catch blocks are collapsed into the highest one
                // The first node of the equivalence class is thus the block that should be transformed
                for (int i = 1; i < identicalStmts.size(); i++) {
                    asCtx(data).addViolation(identicalStmts.get(i), identicalBranchName);
                }
            }
        }

        return data;
    }

}
