/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import net.sourceforge.pmd.lang.kotlin.KotlinLanguageModule;
import net.sourceforge.pmd.lang.rule.xpath.impl.AbstractXPathFunctionDef;

abstract class BaseKotlinXPathFunction extends AbstractXPathFunctionDef {

    protected BaseKotlinXPathFunction(String localName) {
        super(localName, KotlinLanguageModule.TERSE_NAME);
    }
}
