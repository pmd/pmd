/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Detects and flags the occurrences of specific method calls against an
 * instance of a designated class. I.e. String.indexOf. The goal is to be able
 * to suggest more efficient/modern ways of implementing the same function.
 *
 * Concrete subclasses are expected to provide the name of the target class and
 * an array of method names that we are looking for. We then pass judgment on
 * any literal arguments we find in the subclass as well.
 *
 * @author Brian Remedios
 * @version $Revision$
 */
public abstract class AbstractPoorMethodCall extends AbstractJavaRule {
    // FIXME not sure the abstraction is generic enough to be reused as is.

    /**
     * The name of the type the method will be invoked against.
     *
     * @return String
     */
    protected abstract String targetTypename();

    /**
     * Return the names of all the methods we are scanning for, no brackets or
     * argument types.
     *
     * @return String[]
     */
    protected abstract String[] methodNames();

    /**
     * Returns whether the node being sent to the method is OK or not. Return
     * true if you want to record the method call as a violation.
     *
     * @param arg
     *            the node to inspect
     * @return boolean
     */
    protected abstract boolean isViolationArgument(Node arg);

    /**
     * Returns whether the name occurrence is one of the method calls we are
     * interested in.
     *
     * @param occurrence
     *            NameOccurrence
     * @return boolean
     */
    private boolean isNotedMethod(NameOccurrence occurrence) {

        if (occurrence == null) {
            return false;
        }

        String methodCall = occurrence.getImage();
        String[] methodNames = methodNames();

        for (String element : methodNames) {
            if (methodCall.indexOf(element) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method visit.
     *
     * @param node
     *            ASTVariableDeclaratorId
     * @param data
     *            Object
     * @return Object
     * @see net.sourceforge.pmd.lang.java.ast.JavaParserVisitor#visit(ASTVariableDeclaratorId,
     *      Object)
     */
    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!targetTypename().equals(node.getNameDeclaration().getTypeImage())) {
            return data;
        }

        for (NameOccurrence occ : node.getUsages()) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            if (isNotedMethod(jocc.getNameForWhichThisIsAQualifier())) {
                Node parent = jocc.getLocation().getParent().getParent();
                if (parent instanceof ASTPrimaryExpression) {
                    // bail out if it's something like indexOf("a" + "b")
                    if (parent.hasDescendantOfType(ASTAdditiveExpression.class)) {
                        return data;
                    }
                    List<ASTLiteral> literals = parent.findDescendantsOfType(ASTLiteral.class);
                    for (int l = 0; l < literals.size(); l++) {
                        ASTLiteral literal = literals.get(l);
                        if (isViolationArgument(literal)) {
                            addViolation(data, jocc.getLocation());
                        }
                    }
                }
            }
        }
        return data;
    }
}
