/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.Stack;

/**
 * Stack with a limited size, without duplicates, without null value. Used to store recent files.
 *
 * @param <E> Element type
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class LimitedSizeStack<E> extends Stack<E> {

    private final int maxSize;


    public LimitedSizeStack(int maxSize) {
        this.maxSize = maxSize;
    }


    @Override
    public E push(E item) {
        if (item == null) {
            return null;
        }

        if (this.contains(item)) {
            this.remove(item);
        }

        super.push(item);

        if (size() > maxSize) {
            this.removeElementAt(size() - 1);
        }

        return item;
    }
}
