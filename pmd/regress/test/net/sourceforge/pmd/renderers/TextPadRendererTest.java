/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.TextPadRenderer;


public class TextPadRendererTest extends AbstractRendererTst{

    public AbstractRenderer getRenderer() {
        return new TextPadRenderer();
    }

    public String getExpected() {
        return PMD.EOL + "n/a(1,  Foo):  msg";
    }

    public String getExpectedEmpty() {
        return "";
    }
    
    public String getExpectedMultiple() {
        return PMD.EOL + "n/a(1,  Foo):  msg" + PMD.EOL + "n/a(1,  Foo):  msg";
    }

}









