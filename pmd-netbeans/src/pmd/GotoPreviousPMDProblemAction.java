package pmd;

import org.openide.util.NbBundle;

public class GotoPreviousPMDProblemAction extends GotoNextPMDProblemAction {
    
    protected boolean action(){
        return !OutputWindow.getInstance().selectPreviousResult();
    }
    
    public String getName() {
        return NbBundle.getMessage(GotoPreviousPMDProblemAction.class, "CTL_GotoPreviousPMDProblemAction");
    }
}
