/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.List;

import org.jaxen.JaxenException;

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
    default Node jjtGetParent() {
        return null; // TODO: review this
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
    default Node jjtGetChild(final int index) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default int jjtGetNumChildren() {
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

    @Override
    default Node getNthParent(final int n) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> T getFirstParentOfType(final Class<T> parentType) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> List<T> getParentsOfType(final Class<T> parentType) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> T getFirstParentOfAnyType(final Class<? extends T>[] parentTypes) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> List<T> findChildrenOfType(final Class<T> childType) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> List<T> findDescendantsOfType(final Class<T> targetType) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> void findDescendantsOfType(final Class<T> targetType, final List<T> results,
        final boolean crossFindBoundaries) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> T getFirstChildOfType(final Class<T> childType) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> T getFirstDescendantOfType(final Class<T> descendantType) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default <T> boolean hasDescendantOfType(final Class<T> type) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default List<? extends Node> findChildNodesWithXPath(final String xpathString) throws JaxenException {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }

    @Override
    default boolean hasDescendantMatchingXPath(final String xpathString) {
        throw new UnsupportedOperationException("Out of scope for antlr current implementations");
    }
}
