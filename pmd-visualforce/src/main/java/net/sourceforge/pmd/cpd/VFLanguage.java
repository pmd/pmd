/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

public class VFLanguage extends AbstractLanguage {
    public VFLanguage() {
        super("VisualForce", "vf", new VFTokenizer(), ".page", ".component");
    }
}
