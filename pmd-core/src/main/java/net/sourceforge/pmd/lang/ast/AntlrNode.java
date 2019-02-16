package net.sourceforge.pmd.lang.ast;

/**
 * Base interface for all Antlr-based implementation of Node interface.
 *
 * Initially all the methods implemented here will be no-op due to scope limitations
 */
public interface AntlrNode extends Node {

    @Override
    default void jjtOpen() {

    }

    @Override
    default void jjtClose() {

    }

    @Override
    default void jjtSetParent(final Node parent) {

    }

    @Override
    default Node jjtGetParent() {
        return null;
    }

    @Override
    default void jjtAddChild(final Node child, final int index) {

    }

    @Override
    default void jjtSetChildIndex(final int index) {

    }

    @Override
    default int jjtGetChildIndex() {
        return 0;
    }

    @Override
    default Node jjtGetChild(final int index) {
        return null;
    }

    @Override
    default int jjtGetNumChildren() {
        return 0;
    }

    @Override
    default int jjtGetId() {
        return 0;
    }

    @Override
    default String getImage() {
        return null;
    }

    @Override
    default void setImage(final String image) {

    }

    @Override
    default boolean hasImageEqualTo(final String image) {
        return false;
    }

    @Override
    default void remove() {

    }

    @Override
    default void removeChildAtIndex(final int childIndex) {

    }
}
