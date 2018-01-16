/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

/**
 * <p>
 * Enum describing all possible types of {@link RewriteEvent}s.
 * </p>
 * <p>
 * Each type holds a <strong>valid*</strong> index position, so as to ensure that one can build arrays
 * using these types for accessing its elements.
 * </p>
 * <p>
 * <strong>*valid</strong> means that each type holds a unique index in the range
 * {@code [0, RewriteEventType.values() - 1]}.
 * </p>
 */
public enum RewriteEventType {
    INSERT(0), REMOVE(1), REPLACE(2);

    private final int index;

    RewriteEventType(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
