/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Base implementation.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractJScope implements JScope {

    private static final Iterator<?> EMPTY_ITERATOR = new Iterator<Object>() {
        @Override
        public boolean hasNext() {
            return false;
        }


        @Override
        public Object next() {
            throw new NoSuchElementException("Empty iterator!");
        }
    };
    private final JScope parent;


    /**
     * Constructor with just the parent scope.
     *
     * @param parent Parent scope
     */
    AbstractJScope(JScope parent) {
        this.parent = parent;
    }


    @Override
    public JScope getParent() {
        return parent;
    }


    protected abstract Optional<JSymbolicClassReference> resolveTypeNameImpl(String simpleName);


    protected abstract Iterator<JMethodReference> resolveMethodNameImpl(String simpleName);


    protected abstract Optional<JVarReference> resolveValueNameImpl(String simpleName);


    @Override
    public Optional<JSymbolicClassReference> resolveTypeName(String simpleName) {
        Optional<JSymbolicClassReference> result = resolveTypeNameImpl(simpleName);
        return result.isPresent() ? result : parent.resolveTypeName(simpleName);
    }


    @Override
    public final Optional<JVarReference> resolveValueName(String simpleName) {
        Optional<JVarReference> result = resolveValueNameImpl(simpleName);
        return result.isPresent() ? result : parent.resolveValueName(simpleName);
    }


    @Override
    public final Iterator<JMethodReference> resolveMethodName(String simpleName) {
        return chain(resolveMethodNameImpl(simpleName), () -> parent.resolveMethodName(simpleName));
    }


    @SuppressWarnings("unchecked")
    protected static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }


    private static <T> Iterator<T> chain(Iterator<? extends T> fst, Supplier<Iterator<? extends T>> sndSupplier) {
        return new Iterator<T>() {

            Iterator<? extends T> snd;


            @Override
            public boolean hasNext() {
                if (fst.hasNext()) {
                    return true;
                } else if (snd != null) {
                    return snd.hasNext();
                } else if (snd == null) {
                    snd = sndSupplier.get();
                    return snd.hasNext();
                } else {
                    return false;
                }
            }


            @Override
            public T next() {
                if (fst.hasNext()) {
                    return fst.next();
                } else if (snd == null) {
                    throw new IllegalStateException("You should have called hasNext");
                } else if (snd.hasNext()) {
                    return snd.next();
                } else {
                    throw new NoSuchElementException("Empty iterator!");
                }
            }
        };
    }
}
