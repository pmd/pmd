/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;

/**
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {
        // internal
    }

    public static JavaccTokenDocument.TokenDocumentBehavior getJspTokenBehavior() {
        return JspParser.TOKEN_BEHAVIOR;
    }
}
