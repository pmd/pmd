package net.sourceforge.pmd.eclipse.views;

import org.eclipse.jface.action.Action;

/**
 * Implements the remove violation action
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class RemoveViolationAction extends Action {
    private ViolationView violationView;

    /**
     * Constructor
     */
    public RemoveViolationAction(ViolationView violationView) {
        this.violationView = violationView;
    }
    
    
    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        violationView.removeSelectedViolation();
    }

}
