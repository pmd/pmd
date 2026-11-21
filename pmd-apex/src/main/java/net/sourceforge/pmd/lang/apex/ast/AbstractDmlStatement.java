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

    /**
     * Access mode of an Apex DML statement.
     *
     * @since 7.30.0
     */
    public enum AccessMode {
        IMPLICIT,
        USER_MODE,
        SYSTEM_MODE
    }

    /**
     * Returns the access mode of this DML statement.
     * The result is {@link AccessMode#IMPLICIT} when the statement has no {@code as user} or {@code as system} clause.
     *
     * @since 7.30.0
     */
    public AccessMode getAccessMode() {
        DmlStatement.AccessLevel accessLevel = node.getAccess();
        if (accessLevel == null) {
            return AccessMode.IMPLICIT;
        }
        return AccessMode.valueOf(accessLevel.name());
    }

    /**
     * @deprecated Since 7.30.0. Use {@link #getAccessMode()} instead. This method will be removed in PMD 8.0.0.
     */
    @Deprecated
    public Optional<String> getRunAsMode() {
        AccessMode accessMode = getAccessMode();
        if (accessMode == AccessMode.IMPLICIT) {
            return Optional.empty();
        }
        return Optional.of(accessMode.name());
    }
}
