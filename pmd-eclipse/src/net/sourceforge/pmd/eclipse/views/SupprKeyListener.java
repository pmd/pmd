package net.sourceforge.pmd.eclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

/**
 * Implements the action to table on suppr key press
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class SupprKeyListener extends KeyAdapter {
    private ViolationView violationView;
    
    /**
     * Constructor
     */
    public SupprKeyListener(ViolationView violationView) {
        this.violationView = violationView;
    }

    /**
     * @see org.eclipse.swt.events.KeyListener#keyPressed(KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        if ((e.character == SWT.DEL) && (e.stateMask == 0)) {
            violationView.removeSelectedViolation();
        }
    }

}
