package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * A wizard that encapsulates a succession of rule panel managers to collect the
 * required info for a new rule.
 * 
 * @author Brian Remedios
 */
public class CreateRuleWizard extends Wizard implements ValueChangeListener, RuleTarget {

	private Rule 		 rule;
	private WizardDialog dialog;
	
	public CreateRuleWizard() {
	  super();
	}

	public void dialog(WizardDialog theDialog) {
		dialog = theDialog;
	}
	
	public Rule rule() {
	   return rule;
	}

	public void rule(Rule theRule) {
		rule = theRule;
		dialog.updateButtons();
	}

	public void addPages() {
	  addPage(new RulePanelManager("rule", EditorUsageMode.CreateNew, this, this));
	  addPage(new DescriptionPanelManager("description", EditorUsageMode.CreateNew, this));
	  addPage(new PerRulePropertyPanelManager("properties", EditorUsageMode.CreateNew, this));
	  addPage(new XPathPanelManager("xpath", EditorUsageMode.CreateNew, this));
	  addPage(new ExclusionPanelManager("exclusion", EditorUsageMode.CreateNew, this, false));
//	  addPage(new QuickFixPanelManager("fixes", EditorUsageMode.CreateNew, this));
	  addPage(new ExamplePanelManager("examples", EditorUsageMode.CreateNew, this));
	}

	public boolean performFinish() {
      return true;
	}

	private boolean isXPathRule() {
		return RuleUtil.isXPathRule(rule);
	}

	public boolean performCancel() {
	    System.out.println("Perform Cancel called");
	    return true;
	}

	private IWizardPage getAndPrepare(String pageId) {

		AbstractRulePanelManager rulePanel = (AbstractRulePanelManager)getPage(pageId);
		RuleSelection rs = new RuleSelection(rule);
		rulePanel.manage(rs);
		return rulePanel;
	}

	public IWizardPage getNextPage(IWizardPage currentPage) {

		if (rule == null) {
			return null;
		}

	    if (currentPage instanceof RulePanelManager) {
	        return getAndPrepare(DescriptionPanelManager.ID);
	      }
	    if (currentPage instanceof DescriptionPanelManager) {
	    	if (StringUtil.isEmpty( rule.getDescription() )) return null;
	        return getAndPrepare(PerRulePropertyPanelManager.ID);
	      }
	    if (currentPage instanceof PerRulePropertyPanelManager) {
	    	return isXPathRule() ?
	    		getAndPrepare(XPathPanelManager.ID) :
	    		getAndPrepare(ExclusionPanelManager.ID);
	      }
	    if (currentPage instanceof ExclusionPanelManager || currentPage instanceof XPathPanelManager) {
//	        return getAndPrepare(QuickFixPanelManager.ID);
	        return getAndPrepare(ExamplePanelManager.ID);
	      }
	    if (currentPage instanceof QuickFixPanelManager) {
	        return getAndPrepare(ExamplePanelManager.ID);
	      }
	    return null;
	}

	public void changed(RuleSelection rule, PropertyDescriptor<?> desc, Object newValue) {
		// TODO Auto-generated method stub

	}

	public void changed(Rule rule, PropertyDescriptor<?> desc, Object newValue) {
		// TODO Auto-generated method stub

	}
}