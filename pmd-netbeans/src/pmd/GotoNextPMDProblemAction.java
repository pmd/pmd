package pmd;

import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public class GotoNextPMDProblemAction extends CallableSystemAction {
    
    public void performAction() {
        if (action()){
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(GotoPreviousPMDProblemAction.class, "MSG_NoResults"));
        }
    }
    
    protected boolean action(){
        return !OutputWindow.getInstance().selectNextResult();
    }
    
    public String getName() {
        return NbBundle.getMessage(GotoNextPMDProblemAction.class, "CTL_GotoNextPMDProblemAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
