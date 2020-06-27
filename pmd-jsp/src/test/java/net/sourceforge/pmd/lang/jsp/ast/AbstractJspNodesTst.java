/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.PmdContextualizedTest;

public abstract class AbstractJspNodesTst extends PmdContextualizedTest {

    protected JspParsingHelper jsp = JspParsingHelper.DEFAULT.withResourceContext(getClass())
                                                             .withLanguageRegistry(languageRegistry());

}
