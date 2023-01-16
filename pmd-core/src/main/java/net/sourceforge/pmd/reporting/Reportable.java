/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Interface implemented by those objects that can be the target of
 * a {@link RuleViolation}. {@link Node}s and {@link GenericToken tokens}
 * implement this interface.
 *
 * TODO use this in RuleViolationFactory
 */
public interface Reportable {

    // todo add optional method to get the nearest node, to implement
    //  suppression that depends on tree structure (eg annotations) for
    //  not just nodes, for example, for comments or individual tokens

    /**
     * Returns the location at which this element should be reported.
     *
     * <p>Use this instead of {@link #getBeginColumn()}/{@link #getBeginLine()}, etc.
     */
    FileLocation getReportLocation();


    /**
     * Gets the line where the token's region begins
     *
     * @deprecated Use {@link #getReportLocation()}
     */
    @Deprecated
    @DeprecatedUntil700
    default int getBeginLine() {
        return getReportLocation().getStartPos().getLine();
    }


    /**
     * Gets the line where the token's region ends
     *
     * @deprecated Use {@link #getReportLocation()}
     */
    @Deprecated
    @DeprecatedUntil700
    default int getEndLine() {
        return getReportLocation().getEndPos().getLine();
    }


    /**
     * Gets the column offset from the start of the begin line where the token's region begins
     *
     * @deprecated Use {@link #getReportLocation()}
     */
    @Deprecated
    @DeprecatedUntil700
    default int getBeginColumn() {
        return getReportLocation().getStartPos().getColumn();
    }


    /**
     * Gets the column offset from the start of the end line where the token's region ends
     *
     * @deprecated Use {@link #getReportLocation()}
     */
    @Deprecated
    @DeprecatedUntil700
    default int getEndColumn() {
        return getReportLocation().getEndPos().getColumn();
    }



}
