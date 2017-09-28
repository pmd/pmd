/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Set;

/**
 * Represents the start or end of a style span.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SpanBound implements Comparable<SpanBound> {

    private final Set<String> cssClasses;
    private final boolean isBeginBound;
    private int position;


    /**
     * This
     * @param position
     * @param cssClasses
     * @param isBeginBound
     */
    SpanBound(int position, Set<String> cssClasses, boolean isBeginBound) {
        this.position = position;
        this.cssClasses = cssClasses;
        this.isBeginBound = isBeginBound;
    }


    public Set<String> getCssClasses() {
        return cssClasses;
    }


    public boolean isBeginBound() {
        return isBeginBound;
    }


    public int getPosition() {
        return position;
    }


    @Override
    public int compareTo(SpanBound o) {
        return Integer.compare(position, o.position);
    }


}
