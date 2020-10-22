/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for Scala.
 */
public class ScalaLanguage extends AbstractLanguage {

    /**
     * Creates a new Scala Language instance.
     */
    public ScalaLanguage() {
        super("Scala", "scala", new ScalaTokenizer(), ".scala");
    }
}
