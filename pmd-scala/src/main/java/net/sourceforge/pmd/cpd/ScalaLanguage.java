/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import org.sonar.plugins.scala.cpd.ScalaTokenizer;

/**
 * Language implementation for Scala
 */
public class ScalaLanguage extends AbstractLanguage {

    /**
     * Creates a new Scala Language instance.
     */
    public ScalaLanguage() {
        super("Scala", "scala", new ScalaTokenizer(), ".scala");
    }
}
