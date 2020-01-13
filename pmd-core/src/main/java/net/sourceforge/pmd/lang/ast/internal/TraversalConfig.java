/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


public final class TraversalConfig {


    private final boolean crossFindBoundaries;

    public TraversalConfig(boolean crossFindBoundaries) {
        this.crossFindBoundaries = crossFindBoundaries;
    }

}
