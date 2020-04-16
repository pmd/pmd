/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import net.sourceforge.pmd.lang.ast.xpath.internal.AbstractXPathFunctionDef;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

abstract class BaseJavaXPathFunction extends AbstractXPathFunctionDef {

    protected BaseJavaXPathFunction(String localName) {
        super(localName, JavaLanguageModule.TERSE_NAME);
    }
}
