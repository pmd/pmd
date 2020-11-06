/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Will be removed with 7.0.0. See https://github.com/pmd/pmd/issues/997 and https://github.com/pmd/pmd/issues/910
 */
@Deprecated
public interface Dimensionable {
    @Deprecated
    boolean isArray();

    @Deprecated
    int getArrayDepth();
}
