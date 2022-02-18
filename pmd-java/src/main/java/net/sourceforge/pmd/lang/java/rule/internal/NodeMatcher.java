package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.function.Predicate;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * A pattern to match over nodes.
 *
 * @author Clément Fournier
 */
public interface NodeMatcher<T extends Node> extends Predicate<T> {

    /**
     * Returns true if this pattern matches the given node.
     */
    boolean matches(T t);


    @Override
    default boolean test(T t) {
        return matches(t);
    }
}
