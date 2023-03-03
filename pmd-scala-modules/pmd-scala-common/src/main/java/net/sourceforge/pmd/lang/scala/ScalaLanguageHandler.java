/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.scala.ast.ScalaParser;

/**
 * The Scala Language Handler implementation.
 */
public class ScalaLanguageHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public ScalaParser getParser() {
        return new ScalaParser();
    }
}
