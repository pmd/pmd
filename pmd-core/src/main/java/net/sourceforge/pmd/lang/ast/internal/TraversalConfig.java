/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;

/**
 * Configuration object for a tree traversal.
 *
 * @see DescendantNodeStream
 */
final class TraversalConfig {

    private static final TraversalConfig CROSS = new TraversalConfig(true);
    private static final TraversalConfig DONT_CROSS = new TraversalConfig(false);

    /**
     * Default traversal config used by methods like {@link Node#descendants()}
     */
    static final TraversalConfig DEFAULT = DONT_CROSS;

    private final boolean crossFindBoundaries;

    private TraversalConfig(boolean crossFindBoundaries) {
        this.crossFindBoundaries = crossFindBoundaries;
    }

    public boolean isCrossFindBoundaries() {
        return crossFindBoundaries;
    }

    TraversalConfig crossFindBoundaries(boolean cross) {
        return cross ? CROSS : DONT_CROSS;
    }

    /**
     * Apply this configuration to the given node stream.
     */
    <T extends Node> DescendantNodeStream<T> apply(DescendantNodeStream<T> stream) {
        return stream.crossFindBoundaries(crossFindBoundaries);
    }

}
