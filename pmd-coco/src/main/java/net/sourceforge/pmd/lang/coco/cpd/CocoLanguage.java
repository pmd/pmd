/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.coco.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

/**
 * Language implementation for Coco.
 */
public class CocoLanguage extends AbstractLanguage {

    /**
     * Creates a new Coco Language instance.
     */
    public CocoLanguage() {
        super("Coco", "coco", new CocoTokenizer(), ".coco");
    }
}
