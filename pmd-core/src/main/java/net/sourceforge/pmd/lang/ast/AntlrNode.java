/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Base interface for all Antlr-based implementation of Node interface.
 * <p>
 * Initially all the methods implemented here will be no-op due to scope limitations
 */
public interface AntlrNode extends Node {

    @Override
    default void jjtOpen() {
        throw new UnsupportedOperationException("Won't be needed on Antlr implementation");
    }

    @Override
    default void jjtClose() {
        throw new UnsupportedOperationException("Won't be needed on Antlr implementation");
    }

    @Override
    default void jjtSetParent(final Node parent) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default void jjtAddChild(final Node child, final int index) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default void jjtSetChildIndex(final int index) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default int jjtGetChildIndex() {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default int jjtGetId() {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default String getImage() {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default void setImage(final String image) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default boolean hasImageEqualTo(final String image) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default void remove() {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default void removeChildAtIndex(final int childIndex) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }
}
