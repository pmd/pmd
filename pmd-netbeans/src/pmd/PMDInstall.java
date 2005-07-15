package pmd;

import org.openide.modules.ModuleInstall;

/** Extends default set of rules avaliable in PMD.
 *
 * @author Radim Kubacki
 */
public class PMDInstall extends ModuleInstall {
    
    /** Adds custom RuleSetFactory when IDE is started. */
    public void restored () {
        pmd.config.ConfigUtils.addRuleSetFactory (NbRuleSetFactory.getDefault ());
    }
    
    /** Unregisters RuleSetFactory. */
    public void uninstalled () {
        pmd.config.ConfigUtils.removeRuleSetFactory (NbRuleSetFactory.getDefault ());
    }
}
