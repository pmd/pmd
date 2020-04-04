/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.Iterator;
import java.util.List;

/**
 * @author Clément Fournier
 */
public interface ShadowIterator<S, I> extends Iterator<List<S>> {

    @Override
    boolean hasNext();


    @Override
    List<S> next(); // nonempty


    I getScopeTag();


    List<S> getResults();

}
