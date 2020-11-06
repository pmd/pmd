/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Language implementation for Dart
 */
public class DartLanguage extends AbstractLanguage {

    /**
     * Creates a new Dart Language instance.
     */
    public DartLanguage() {
        super("Dart", "dart", new DartTokenizer(), ".dart");
    }
}
