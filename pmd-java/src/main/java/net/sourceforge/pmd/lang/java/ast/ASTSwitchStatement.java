/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Represents a {@code switch} statement. See {@link ASTSwitchLike} for
 * its grammar.
 */
public final class ASTSwitchStatement extends AbstractStatement implements ASTSwitchLike {

    ASTSwitchStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public FileLocation getReportLocation() {
        return getFirstToken().getReportLocation();
    }

}
