/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 4:29:19 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {

    private static final String NAME = "PMD Options";

    public PMDOptionPane() {
        super(NAME);
    }

    public String getName() {
        return NAME;
    }

    public void _init() {
        throw new RuntimeException("HI!!!!");
/*
        for (int i = 0; i < jEdit.getViews().length; i++) {
            View v  = jEdit.getViews()[i];
            v.getStatus().setMessage("HOWDY");
        }
*/
    }

}
