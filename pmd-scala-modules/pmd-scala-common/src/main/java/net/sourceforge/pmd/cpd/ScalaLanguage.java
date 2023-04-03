/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

/**
 * Language implementation for Scala.
 */
public class ScalaLanguage extends AbstractLanguage {

    /**
     * Creates a new Scala Language instance.
     */
    public ScalaLanguage() {
        super(ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, new ScalaTokenizer(), ScalaLanguageModule.EXTENSIONS);
    }
}
