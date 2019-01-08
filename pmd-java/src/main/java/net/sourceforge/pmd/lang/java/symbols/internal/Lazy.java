/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * Lazy value utility.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
final class Lazy<T> {

    private final Supplier<T> mySupplier;
    private T myT;
    // todo maybe make this thread safe when we cache symbols globally


    Lazy(Supplier<T> mySupplier) {
        this.mySupplier = Objects.requireNonNull(mySupplier);
    }


    Lazy(T alreadyResolved) {
        this.mySupplier = null;
        this.myT = alreadyResolved;
    }


    public boolean isResolved() {
        return myT != null;
    }


    public T getValue() {
        if (!isResolved()) {
            myT = Objects.requireNonNull(mySupplier.get());
        }
        return myT;
    }
}
