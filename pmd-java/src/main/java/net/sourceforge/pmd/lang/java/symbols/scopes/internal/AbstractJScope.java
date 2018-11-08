/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Base implementation.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractJScope implements JScope {

    private final JScope parent;


    AbstractJScope(JScope parent) {
        this.parent = parent;
    }


    @Override
    public JScope getParent() {
        return parent;
    }
}
