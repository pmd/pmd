/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Interface implemented by those objects that can be the target of
 * a {@link RuleViolation}. {@link Node}s and {@link GenericToken}s
 * should implement this interface, eventually.
 */
public interface Reportable {

    /**
     * Returns the location for which the file is reported.
     */
    FileLocation getReportLocation();


}
