/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class VariableNameDeclaration extends AbstractNameDeclaration {
    private static final Logger LOGGER = Logger.getLogger(VariableNameDeclaration.class.getName());

    public VariableNameDeclaration(ASTVariableOrConstantDeclaratorId node) {
        super(node);
    }

    @Override
    public Scope getScope() {
        try {
            return node.getScope().getEnclosingScope(ClassScope.class);
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("This Node does not have an enclosing Class: " + node.getBeginLine() + "/"
                        + node.getBeginColumn() + " => " + this.getImage());
            }
            return null; // @TODO SRT a cop-out
        }
    }

    public ASTVariableOrConstantDeclaratorId getDeclaratorId() {
        return (ASTVariableOrConstantDeclaratorId) node;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VariableNameDeclaration)) {
            return false;
        }
        VariableNameDeclaration n = (VariableNameDeclaration) o;
        try {
            return n.getImage().equals(this.getImage());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("n.node=" + n.node);
                LOGGER.finest("n.getImage=" + n.getImage());
                LOGGER.finest("node=" + node);
                LOGGER.finest("this.getImage=" + this.getImage());
            }
            return false;
        }
    }

    @Override
    public int hashCode() {
        try {
            return this.getImage().hashCode();
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("VariableNameDeclaration: node=" + node);
                LOGGER.finest("VariableNameDeclaration: node,getImage=" + this.getImage());
            }
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Variable: image = '" + node.getImage() + "', line = " + node.getBeginLine();
    }
}
