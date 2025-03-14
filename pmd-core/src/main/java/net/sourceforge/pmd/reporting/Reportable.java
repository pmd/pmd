/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Interface implemented by those objects that can be the target of
 * a {@link RuleViolation}. {@link Node}s and {@link GenericToken tokens}
 * implement this interface.
 */
// TODO use this in RuleContext where RuleViolations are created
public interface Reportable {

    // todo add optional method to get the nearest node, to implement
    //  suppression that depends on tree structure (eg annotations) for
    //  not just nodes, for example, for comments or individual tokens

    /**
     * Returns the location at which this element should be reported.
     *
     * <p>Use this instead of {@link Node#getBeginColumn()}/{@link Node#getBeginLine()}, etc.
     */
    FileLocation getReportLocation();
}
