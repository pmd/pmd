/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.Comparator;

/**
 * Compares RuleViolations using the following criteria:
 * <ol>
 *    <li>Source file name</li>
 *    <li>Begin line</li>
 *    <li>Description</li>
 *    <li>Begin column</li>
 *    <li>End line</li>
 *    <li>End column</li>
 *    <li>Rule name</li>
 * </ol>
 */
public final class RuleViolationComparator implements Comparator<RuleViolation> {

    public static final RuleViolationComparator INSTANCE = new RuleViolationComparator();

    private RuleViolationComparator() {
    }

    public int compare(RuleViolation r1, RuleViolation r2) {
	if (!r1.getFilename().equals(r2.getFilename())) {
	    return r1.getFilename().compareTo(r2.getFilename());
	}
	if (r1.getBeginLine() != r2.getBeginLine()) {
	    return r1.getBeginLine() - r2.getBeginLine();
	}
	if (r1.getDescription() != null && r2.getDescription() != null
		&& !r1.getDescription().equals(r2.getDescription())) {
	    return r1.getDescription().compareTo(r2.getDescription());
	}
	if (r1.getBeginColumn() != r2.getBeginColumn()) {
	    return r1.getBeginColumn() - r2.getBeginColumn();
	}
	if (r1.getEndLine() != r2.getEndLine()) {
	    return r1.getEndLine() - r2.getEndLine();
	}
	if (r1.getEndColumn() != r2.getEndColumn()) {
	    return r1.getEndColumn() - r2.getEndColumn();
	}
	return r1.getRule().getName().compareTo(r2.getRule().getName());
    }
}
