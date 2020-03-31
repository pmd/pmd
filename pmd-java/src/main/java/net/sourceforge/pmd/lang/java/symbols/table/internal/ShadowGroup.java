/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

/**
 * @author Clément Fournier
 */
public class ShadowGroup<S> {

    private final PMultimap<String, S> symbols;


    public ShadowGroup(PMultimap<String, S> symbols) {
        this.symbols = symbols;
    }
}
