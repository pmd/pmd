/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.PmdContextualizedTest;

public abstract class AbstractVfTest extends PmdContextualizedTest {

    protected final VfParsingHelper vf =
        VfParsingHelper.DEFAULT
            .withResourceContext(getClass())
            .withLanguageRegistry(languageRegistry());

}
