/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.vf.IdentifierType;

public class ASTIdentifier extends AbstractVFNode {
    /**
     * The data type that this identifier refers to. May be null.
     */
    private IdentifierType identifierType;

    @Deprecated
    @InternalApi
    public ASTIdentifier(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTIdentifier(VfParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(IdentifierType identifierType) {
        this.identifierType = identifierType;
    }
}
