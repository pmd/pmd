/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class SoftReferenceCache<T> {

    private final Supplier<T> mySupplier;
    private SoftReference<T> myRef;


    public SoftReferenceCache(Supplier<T> supplier) {
        this.mySupplier = supplier;
    }


    public T getValue() {
        if (myRef == null || myRef.get() == null) {
            T val = mySupplier.get();
            if (val == null) {
                throw new IllegalStateException();
            }

            myRef = new SoftReference<>(val);
            return val;
        }

        return myRef.get();
    }


}
