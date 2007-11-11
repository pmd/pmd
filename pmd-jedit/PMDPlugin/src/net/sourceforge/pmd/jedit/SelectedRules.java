/*
 *  User: tom
 *  Date: Jul 9, 2002
 *  Time: 1:18:38 PM
 */
package net.sourceforge.pmd.jedit;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;

import org.gjt.sp.jedit.jEdit;


/**
 *  Description of the Class
 *
 * @author     jiger.p
 * @created    April 22, 2003
 */
public class SelectedRules
{

	private final Set<RuleCheckBox> checkboxes = new TreeSet<RuleCheckBox>(
							new Comparator<RuleCheckBox>()
							{
								public int compare(RuleCheckBox r1, RuleCheckBox r2)
								{
									return r1.getRule().getName().compareTo(r2.getRule().getName());
								}
							});


	/**
	 *  Constructor for the SelectedRules object
	 *
	 * @exception  RuleSetNotFoundException  Description of the Exception
	 */
	public SelectedRules() throws RuleSetNotFoundException
	{
		RuleSetFactory rsf = new RuleSetFactory();
		for(Iterator<RuleSet> i = rsf.getRegisteredRuleSets(); i.hasNext(); )
		{
			RuleSet rs = i.next();
			//System.out.println("Added RuleSet " + rs.getName() + " descriprion "+ rs.getDescription() +" language "+ rs.getLanguage());
			addRuleSet2Rules(rs);
		}

		//Load custom RuleSets if any.

		String customRuleSetPath = jEdit.getProperty("pmd.customRulesPath");

		if(!(customRuleSetPath == null))
		{
			RuleSets ruleSets = rsf.createRuleSets(customRuleSetPath);
			
			if(ruleSets.getAllRuleSets() != null)
			{
				for(int i=0;i<ruleSets.getAllRuleSets().length;i++)
				{
					RuleSet rs = ruleSets.getAllRuleSets()[i];
					addRuleSet2Rules(rs);
				}
			}
		}
	}


	/**
	 *  Gets the allBoxes attribute of the SelectedRules object
	 *
	 * @return    The allBoxes value
	 */
	public RuleCheckBox[] getAllBoxes()
	{
		return checkboxes.toArray(new RuleCheckBox[checkboxes.size()]);
	}


	/**  Description of the Method */
	public void save()
	{
		for(RuleCheckBox box: checkboxes)
		{
			jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + box.getRule().getName(), box.isSelected());
		}
	}


	/**
	 *  Gets the selectedRules attribute of the SelectedRules object
	 *
	 * @return    The selectedRules value
	 */
	public RuleSets getSelectedRules()
	{
		RuleSets newRuleSets = new RuleSets();
		
		Map<String, RuleSet> rulesetmap = new HashMap<String, RuleSet>();
		
		for(RuleCheckBox box: checkboxes)
		{
			if(box.isSelected())
			{
				Rule rule = box.getRule();
				RuleSet rs = box.getRuleset();
				if(rulesetmap.get(rs.getName()) != null)
				{
					RuleSet existingRs = (RuleSet) rulesetmap.get(rs.getName());
					existingRs.addRule(rule);
				}
				else
				{
					//Create a new RuleSet and register in RuleSets and the Map. We copy critical attributes such as name, language which will be used for processing by PMD.
					RuleSet newrs = new RuleSet();
					newrs.setName(rs.getName());
					newrs.setDescription(rs.getDescription());
					newrs.setLanguage(rs.getLanguage());
					
					newrs.addRule(rule);
					rulesetmap.put(rs.getName(), newrs);
					newRuleSets.addRuleSet(newrs);
				}
			}
		}
		return newRuleSets;
	}

	/**
	 *  Adds a feature to the RuleSet2Rules attribute of the SelectedRules object
	 *
	 * @param  rs  The feature to be added to the RuleSet2Rules attribute
	 */
	private void addRuleSet2Rules(RuleSet rs)
	{
		for(Rule rule: rs.getRules())
		{
			checkboxes.add(new RuleCheckBox(rule, rs));
		}
	}
}

