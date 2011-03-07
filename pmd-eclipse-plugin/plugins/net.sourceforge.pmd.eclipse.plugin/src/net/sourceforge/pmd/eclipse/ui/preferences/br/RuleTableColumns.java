package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.util.StyledTextBuilder;
import net.sourceforge.pmd.eclipse.util.TextAsColourShapeBuilder;
import net.sourceforge.pmd.eclipse.util.UniqueItemsAsShapeBuilder;

import org.eclipse.swt.SWT;

/**
 * 
 * @author Brian Remedios
 */
public interface RuleTableColumns {
	
	RuleColumnDescriptor name 		  = new TextColumnDescriptor("tName", StringKeys.PREF_RULESET_COLUMN_RULE_NAME, 	SWT.LEFT, 210, RuleFieldAccessor.name, true, null);
	RuleColumnDescriptor ruleSetName  = new TextColumnDescriptor("tRSName", StringKeys.PREF_RULESET_COLUMN_RULESET_NAME,SWT.LEFT, 160, TextColumnDescriptor.ruleSetNameAcc, true, null);
	RuleColumnDescriptor priority	  = new TextColumnDescriptor("tPriority", StringKeys.PREF_RULESET_COLUMN_PRIORITY, 	SWT.RIGHT,53, RuleFieldAccessor.priority, false, null);
	RuleColumnDescriptor priorityName = new TextColumnDescriptor("tPrioName", StringKeys.PREF_RULESET_COLUMN_PRIORITY, 	SWT.LEFT, 80, RuleFieldAccessor.priorityName, true, null);
	RuleColumnDescriptor since 		  = new TextColumnDescriptor("tSince", StringKeys.PREF_RULESET_COLUMN_SINCE, 		SWT.RIGHT,46, RuleFieldAccessor.since, false, null);
	RuleColumnDescriptor usesDFA 	  = new TextColumnDescriptor("tUsesDFA", StringKeys.PREF_RULESET_COLUMN_DATAFLOW, 	SWT.LEFT, 60, RuleFieldAccessor.usesDFA, false, null);
	RuleColumnDescriptor externalURL  = new TextColumnDescriptor("tExtURL", StringKeys.PREF_RULESET_COLUMN_URL, 		SWT.LEFT, 100, RuleFieldAccessor.url, true, null);
	RuleColumnDescriptor properties   = new TextColumnDescriptor("tProps", StringKeys.PREF_RULESET_COLUMN_PROPERTIES, 	SWT.LEFT, 40, TextColumnDescriptor.propertiesAcc, true, null);
	RuleColumnDescriptor language     = new TextColumnDescriptor("tLang", StringKeys.PREF_RULESET_COLUMN_LANGUAGE, 		SWT.LEFT, 32, RuleFieldAccessor.language, false, null);
	RuleColumnDescriptor ruleType	  = new TextColumnDescriptor("tRType", StringKeys.PREF_RULESET_COLUMN_RULE_TYPE, 	SWT.LEFT, 20, RuleFieldAccessor.ruleType, false, null);
	RuleColumnDescriptor minLangVers  = new TextColumnDescriptor("tMinLang", StringKeys.PREF_RULESET_COLUMN_MIN_VER, 	SWT.LEFT, 30, RuleFieldAccessor.minLanguageVersion, false, null);
	RuleColumnDescriptor maxLangVers  = new TextColumnDescriptor("tMaxLang", StringKeys.PREF_RULESET_COLUMN_MAX_VER, 	SWT.LEFT, 30, RuleFieldAccessor.maxLanguageVersion, false, null);
	RuleColumnDescriptor exampleCount = new TextColumnDescriptor("tXmpCnt", StringKeys.PREF_RULESET_COLUMN_EXAMPLE_CNT, SWT.RIGHT, 20, RuleFieldAccessor.exampleCount, false, null);
	RuleColumnDescriptor fixCount  	  = new TextColumnDescriptor("fixCnt", StringKeys.PREF_RULESET_COLUMN_FIXCOUNT,    	SWT.RIGHT, 25, RuleFieldAccessor.fixCount, false, null);
	RuleColumnDescriptor modCount  	  = new TextColumnDescriptor("modCnt", StringKeys.PREF_RULESET_COLUMN_MODCOUNT,   	SWT.RIGHT, 25, RuleFieldAccessor.nonDefaultProperyCount, false, null);

//	RuleColumnDescriptor violateXPath = new TextColumnDescriptor("Filter", SWT.RIGHT, 20, RuleFieldAccessor.violationXPath, true);

	RuleColumnDescriptor imgPriority  			= new ImageColumnDescriptor("iPriority", StringKeys.PREF_RULESET_COLUMN_PRIORITY, 	SWT.LEFT, 50, RuleFieldAccessor.priority, false, PMDUiConstants.ICON_BUTTON_DIAMOND_WHITE, new UniqueItemsAsShapeBuilder(12, 12, SWT.LEFT, UISettings.shapesByPriority()));
    RuleColumnDescriptor filterViolationRegex	= new ImageColumnDescriptor("iFvReg", StringKeys.PREF_RULESET_COLUMN_FILTERS_REGEX, SWT.LEFT, 25, RuleFieldAccessor.violationRegex, false, PMDUiConstants.ICON_FILTER_R, new TextAsColourShapeBuilder(16, 16, RuleUIUtil.RegexFilterShape));    
    RuleColumnDescriptor filterViolationXPath	= new ImageColumnDescriptor("iFVXp",  StringKeys.PREF_RULESET_COLUMN_FILTERS_XPATH, SWT.LEFT, 25, RuleFieldAccessor.violationXPath, false, PMDUiConstants.ICON_FILTER_X, new TextAsColourShapeBuilder(16, 16, RuleUIUtil.XPathFilterShape));
	RuleColumnDescriptor imgProperties   		= new ImageColumnDescriptor("iProps", StringKeys.PREF_RULESET_COLUMN_PROPERTIES, 	SWT.LEFT, 40, ImageColumnDescriptor.propertiesAcc, false, null, new StyledTextBuilder(RuleUIUtil.ChangedPropertyFont));

}
