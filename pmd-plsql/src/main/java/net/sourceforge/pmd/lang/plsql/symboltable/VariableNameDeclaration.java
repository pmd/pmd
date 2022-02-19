/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class VariableNameDeclaration extends AbstractNameDeclaration {
    private static final Logger LOG = LoggerFactory.getLogger(VariableNameDeclaration.class);

    public VariableNameDeclaration(ASTVariableOrConstantDeclaratorId node) {
        super(node);
    }

    @Override
    public Scope getScope() {
        try {
            return node.getScope().getEnclosingScope(ClassScope.class);
        } catch (Exception e) {
            LOG.trace("This Node does not have an enclosing Class: {}/{} => {}",
                    node.getBeginLine(), node.getBeginColumn(),
                    this.getImage());
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
            LOG.error(e.getMessage(), e);
            LOG.debug("n.node={}", n.node);
            LOG.debug("n.getImage={}", n.getImage());
            LOG.debug("node={}", node);
            LOG.debug("this.getImage={}", this.getImage());
            return false;
        }
    }

    @Override
    public int hashCode() {
        try {
            return this.getImage().hashCode();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            LOG.debug("VariableNameDeclaration: node={}", node);
            LOG.debug("VariableNameDeclaration: node,getImage={}", this.getImage());
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Variable: image = '" + node.getImage() + "', line = " + node.getBeginLine();
    }
}
