/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Optional;

import com.google.summit.ast.statement.DmlStatement;

public abstract class AbstractDmlStatement extends AbstractApexNode.Single<DmlStatement> {
    protected AbstractDmlStatement(DmlStatement node) {
        super(node);
    }

    public Optional<String> getRunAsMode() {
        DmlStatement.AccessLevel accessLevel = node.getAccess();
        if (accessLevel != null) {
            return Optional.of(accessLevel.name());
        }
        return Optional.empty();
    }
}
