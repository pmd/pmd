/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

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
