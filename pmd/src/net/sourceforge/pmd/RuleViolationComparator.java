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

    public int compare(final RuleViolation r1, final RuleViolation r2) {
	int cmp = r1.getFilename().compareTo(r2.getFilename());
	if (cmp == 0) {
	    cmp = r1.getBeginLine() - r2.getBeginLine();
	    if (cmp == 0) {
		cmp = compare(r1.getDescription(), r2.getDescription());
		if (cmp == 0) {
		    cmp = r1.getBeginColumn() - r2.getBeginColumn();
		    if (cmp == 0) {
			cmp = r1.getEndLine() - r2.getEndLine();
			if (cmp == 0) {
			    cmp = r1.getEndColumn() - r2.getEndColumn();
			    if (cmp == 0) {
				cmp = r1.getRule().getName().compareTo(r2.getRule().getName());
			    }
			}
		    }
		}
	    }
	}
	return cmp;
    }

    private static int compare(final String s1, final String s2) {
	// Treat null as larger
	if (s1 == null) {
	    return 1;
	} else if (s2 == null) {
	    return -1;
	} else {
	    return s1.compareTo(s2);
	}
    }
}
