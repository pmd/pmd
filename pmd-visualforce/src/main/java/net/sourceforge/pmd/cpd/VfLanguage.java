/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * @author sergey.gorbaty
 *
 */
public class VfLanguage extends AbstractLanguage {
    public VfLanguage() {
        super("VisualForce", "vf", new VfTokenizer(), ".page", ".component");
    }
}
