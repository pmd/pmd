/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public interface TrailingCommaNode {
    boolean isTrailingComma();

    @Deprecated
    @InternalApi
    void setTrailingComma(boolean tailingComma);
}
