/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.rule.xpath.impl.AbstractXPathFunctionDef;

abstract class BaseJavaXPathFunction extends AbstractXPathFunctionDef {

    protected BaseJavaXPathFunction(String localName) {
        super(localName, JavaLanguageModule.TERSE_NAME);
    }
}
