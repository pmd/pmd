package net.sourceforge.pmd.eclipse.views;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.jface.action.Action;

/**
 * Implements project select action
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class ProjectSelectionAction extends Action {
    private ViolationView violationView;

    /**
     * Constructor for ProjectSelectionAction.
     */
    public ProjectSelectionAction(ViolationView violationView) {
        super();
        this.violationView = violationView;
        setChecked(PMDPlugin.getDefault().getDialogSettings().getBoolean(PMDPlugin.SETTINGS_VIEW_PROJECT_SELECTION));
    }

    /**
     * @see org.eclipse.jface.action.IAction#getStyle()
     */
    public int getStyle() {
        return AS_CHECK_BOX;
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        super.run();
        if (isChecked()) {
            violationView.setFileSelection(false);
        }

        violationView.refresh();
    }

    /**
     * @see org.eclipse.jface.action.IAction#setChecked(boolean)
     */
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        PMDPlugin.getDefault().getDialogSettings().put(PMDPlugin.SETTINGS_VIEW_PROJECT_SELECTION, isChecked());
    }

}
