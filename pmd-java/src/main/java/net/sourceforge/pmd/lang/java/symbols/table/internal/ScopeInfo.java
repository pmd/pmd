/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;

/**
 * A {@linkplain ShadowChainIterator#getScopeTag() scope tag} for java
 * shadow groups. This gives information about why a declaration is in
 * scope.
 */
public enum ScopeInfo {

    ENCLOSING_TYPE,
    INHERITED,
    LOCAL,
    TYPE_PARAM,

    IMPORT_ON_DEMAND,
    SAME_PACKAGE,
    JAVA_LANG,
    SINGLE_IMPORT,

    SAME_FILE,

    FORMAL_PARAM,

}
