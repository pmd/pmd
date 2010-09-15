package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.ui.LabelProvider;
import net.sourceforge.pmd.eclipse.ui.ShapePicker;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ImplementationType;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSelection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleVisitor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.ValueChangeListener;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.TypeText;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author Brian Remedios
 */
public class RulePanelManager extends AbstractRulePanelManager {

    private RuleTarget  target;

	private Text 		nameField;
    private TypeText 	implementationClassField;
    private Combo		ruleSetNameField;

    private Button  	ruleReferenceButton;
    private Combo 		languageCombo;
    private Combo 		priorityCombo;
    private ShapePicker priorityDisplay;
    
    private Label		minLanguageLabel;
    private Label		maxLanguageLabel;
    private Combo		minLanguageVersionCombo;
    private Combo		maxLanguageVersionCombo;

    private Combo		implementationTypeCombo;

    private Button 		usesTypeResolutionButton;
    private Button 		usesDfaButton;
    private List<Label> labels;

    private boolean 	inSetup;
    private Set<String> currentRuleNames;
    
    public static final String ID = "rule";

	public RulePanelManager(String theTitle, EditorUsageMode theMode, ValueChangeListener theListener, RuleTarget theRuleSource) {
		this(ID, theTitle, theMode, theListener, theRuleSource);
	}

	public RulePanelManager(String theId, String theTitle, EditorUsageMode theMode, ValueChangeListener theListener, RuleTarget theRuleSource) {
		super(theId, theTitle, theMode, theListener);

		target = theRuleSource;
	}

	public void showControls(boolean flag) {
        nameField.setVisible(flag);
        implementationTypeCombo.setVisible(flag);
        implementationClassField.setVisible(flag);
        ruleSetNameField.setVisible(flag);
        languageCombo.setVisible(flag);
        priorityCombo.setVisible(flag);
        priorityDisplay.setVisible(flag);
        minLanguageVersionCombo.setVisible(flag);
        maxLanguageVersionCombo.setVisible(flag);
        usesDfaButton.setVisible(flag);
        usesTypeResolutionButton.setVisible(flag);
        for (Label label : labels) label.setVisible(flag);
	}


	@Override
	protected void clearControls() {
		nameField.setText("");
		ruleSetNameField.select(-1);
        implementationClassField.setType(null);
        ruleSetNameField.setText("");
        languageCombo.select(-1);
        priorityCombo.select(-1);
        priorityDisplay.setItems(null);
        minLanguageVersionCombo.select(-1);
        maxLanguageVersionCombo.select(-1);
        usesDfaButton.setSelection(false);
        usesTypeResolutionButton.setSelection(false);
	}

	private void showLanguageVersionFields(Language language) {

		int versionCount = language == null ? 0 : language.getVersions().size();

		boolean hasVersions = versionCount > 1;

		minLanguageLabel.setVisible(hasVersions);
		maxLanguageLabel.setVisible(hasVersions);
		minLanguageVersionCombo.setVisible(hasVersions);
		maxLanguageVersionCombo.setVisible(hasVersions);

		if (hasVersions) {
			List<LanguageVersion> versions = language.getVersions();
			populate(minLanguageVersionCombo, versions);
			populate(maxLanguageVersionCombo, versions);
		}
	}

	private void populate(Combo field, List<LanguageVersion> versions) {
		field.removeAll();
		for (LanguageVersion version : versions) {
			field.add( version.getName() );
		}
	}

	private Set<Comparable<?>> uniquePriorities() {
		if (rules == null) return Collections.emptySet();
		return RuleUtil.uniqueAspects(rules, RuleFieldAccessor.priority);
	}
	
	private String commonLanguageMinVersionName() {

		if (rules == null) return null;

		LanguageVersion version = RuleUtil.commonLanguageMinVersion(rules);
		return version == null ? null : version.getName();
	}

	private String commonLanguageMaxVersionName() {

		if (rules == null) return null;

		LanguageVersion version = (LanguageVersion)RuleUtil.commonAspect(rules, RuleFieldAccessor.maxLanguageVersion);
		
		return version == null ? null : version.getName();
	}

	private String commonPriorityName() {

		if (rules == null) return null;

		RulePriority priority = RuleUtil.commonPriority(rules);
		return priority == null ? null : UISettings.labelFor(priority);
	}

	private boolean allRulesUseTypeResolution() {

		return rules != null && RuleUtil.allUseTypeResolution(rules);
	}

	private boolean allRulesUseDfa() {

		return rules != null && RuleUtil.allUseDfa(rules);
	}

	@Override
	protected void adapt() {

        show(ruleSetNameField, RuleUtil.commonRuleset(rules));

        Language language = RuleUtil.commonLanguage(rules);
        show(languageCombo, language == null ? "" : language.getName());
       
        ImplementationType impType = rules == null ? ImplementationType.Mixed : rules.implementationType();
        implementationType(impType);
        implementationTypeCombo.setEnabled(creatingNewRule());
        
        Class<?> impClass = RuleUtil.commonImplementationClass(rules);
        show(implementationClassField, impClass);
        implementationClassField.setEnabled( impClass != null);
        
        show(priorityCombo, commonPriorityName());
        priorityDisplay.setItems(uniquePriorities().toArray());
        
        show(usesTypeResolutionButton, allRulesUseTypeResolution());
        show(usesDfaButton, allRulesUseDfa());

        showLanguageVersionFields(language);

        show(minLanguageVersionCombo, commonLanguageMinVersionName());
        show(maxLanguageVersionCombo, commonLanguageMaxVersionName());

        Rule soleRule = soleRule();

        if (soleRule == null) {
            shutdown(nameField);
        } else {
            show(nameField, asCleanString(soleRule.getName()));
        }
        
        validate();
    }

	@Override
	protected boolean canManageMultipleRules() {
		return true;
	}

	@Override
	public Control setupOn(Composite parent) {

			inSetup = true;
			
			labels = new ArrayList<Label>();

		    Composite dlgArea = new Composite(parent, SWT.NONE);

	        GridLayout gridLayout = new GridLayout();
	        gridLayout.numColumns = 6;
	        dlgArea.setLayout(gridLayout);

	        Label nameLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_NAME);
	        GridData data = new GridData();
	        data.horizontalSpan = 1;
	        nameLabel.setLayoutData(data);
	        labels.add(nameLabel);

	        nameField = buildNameText(dlgArea);
	        data = new GridData();
	        data.horizontalAlignment = GridData.FILL;
	        data.horizontalSpan = 5;
	        data.grabExcessHorizontalSpace = true;
	        nameField.setLayoutData(data);

	        Label ruleSetNameLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_RULESET_NAME);
	        data = new GridData();
	        data.horizontalSpan = 1;
	        ruleSetNameLabel.setLayoutData(data);
	        labels.add(ruleSetNameLabel);

	        ruleSetNameField = buildRuleSetNameField(dlgArea);
	        data = new GridData();
	        data.horizontalAlignment = GridData.FILL;
	        data.horizontalSpan = 5;
	        data.grabExcessHorizontalSpace = true;
	        ruleSetNameField.setLayoutData(data);
	        
	        Label implTypeLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_IMPLEMENTED_BY);
	        data = new GridData();
	        data.horizontalSpan = 1;
	        implTypeLabel.setLayoutData(data);
	        labels.add(implTypeLabel);

	        implementationTypeCombo = buildImplementationTypeCombo(dlgArea);
	        data = new GridData();
	        data.horizontalAlignment = GridData.FILL;
	        data.horizontalSpan = 5;
	        data.grabExcessHorizontalSpace = true;
	        implementationTypeCombo.setLayoutData(data);

	        Label implementationClassLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_IMPLEMENTATION_CLASS);
	        data = new GridData();
	        data.horizontalSpan = 1;
	        implementationClassLabel.setLayoutData(data);
	        labels.add(implementationClassLabel);

	        implementationClassField = buildImplementationClassField(dlgArea);
	        data = new GridData();
	        data.horizontalAlignment = GridData.FILL;
	        data.horizontalSpan = 5;
	        data.grabExcessHorizontalSpace = true;
	        implementationClassField.setLayoutData(data);

	        buildLabel(dlgArea, null);
	        usesTypeResolutionButton = buildUsesTypeResolutionButton(dlgArea);
	        usesDfaButton = buildUsesDfaButton(dlgArea);
	        data = new GridData();
	        data.horizontalAlignment = GridData.FILL;
	        data.horizontalSpan = 4;
	        data.grabExcessHorizontalSpace = true;
	        usesDfaButton.setLayoutData(data);
	  //      buildLabel(dlgArea, null);

	        Label languageLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_LANGUAGE);
	        data = new GridData();
	        data.horizontalSpan = 1;
	        languageLabel.setLayoutData(data);
	        labels.add(languageLabel);

	        languageCombo = buildLanguageCombo(dlgArea);
	        data = new GridData();
	        data.horizontalAlignment = GridData.BEGINNING;
	        data.horizontalSpan = 1;
	        data.grabExcessHorizontalSpace = false;
	        languageCombo.setLayoutData(data);

	        	GridData lblGD = new GridData();
	        	lblGD.horizontalSpan = 1;
	        	lblGD.horizontalAlignment = SWT.END;
		        
		        GridData cmboGD = new GridData();
		        cmboGD.horizontalAlignment = GridData.FILL;
		        cmboGD.horizontalSpan = 1;
		        cmboGD.grabExcessHorizontalSpace = true;
		        
		        minLanguageLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_LANGUAGE_MIN);
		        minLanguageLabel.setAlignment(SWT.RIGHT);
		        minLanguageLabel.setLayoutData(lblGD);
		        labels.add(minLanguageLabel);

		        minLanguageVersionCombo = buildLanguageVersionCombo(dlgArea, true);
		        minLanguageVersionCombo.setLayoutData(cmboGD);

		        maxLanguageLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_LANGUAGE_MAX);
		        maxLanguageLabel.setAlignment(SWT.RIGHT);
		        maxLanguageLabel.setLayoutData(lblGD);
		        labels.add(maxLanguageLabel);

		        maxLanguageVersionCombo = buildLanguageVersionCombo(dlgArea, false);
		        maxLanguageVersionCombo.setLayoutData(cmboGD);

		    Label priorityLabel = buildLabel(dlgArea, StringKeys.PREF_RULEEDIT_LABEL_PRIORITY);
	        data = new GridData();
	        data.horizontalSpan = 1;
	        priorityLabel.setLayoutData(data);
	        labels.add(priorityLabel);
	        
	        priorityCombo = buildPriorityCombo(dlgArea);
	        priorityCombo.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	        
	        priorityDisplay = new ShapePicker(dlgArea, SWT.NONE, 14);
	        priorityDisplay.setLayoutData( new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));		     
	        priorityDisplay.setShapeMap(UISettings.shapesByPriority());
	        priorityDisplay.tooltipProvider( new LabelProvider() { 
	        	public String labelFor(Object item) { 
	        		return UISettings.labelFor((RulePriority)item); 
	        		} 
	        	} );
	        priorityDisplay.setSize(120, 25);

	        if (creatingNewRule()) {
	        	implementationType(ImplementationType.XPath);
	        }

	        setControl(dlgArea);
	        
	        validate();

	        inSetup = false;
	        
	        return dlgArea;
		}

	  /**
     * Build the rule reference button
     */
    private Button buildRuleReferenceButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(SWTUtil.stringFor(StringKeys.PREF_RULEEDIT_BUTTON_RULE_REFERENCE));
        button.setEnabled(false);
//        button.setSelection(rule() instanceof RuleReference);
        return button;
    }

    private Label buildLabel(Composite parent, String msgKey) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(msgKey == null ? "" : SWTUtil.stringFor(msgKey));
        return label;
    }

    private Text buildNameText(Composite parent) {

    	int style = creatingNewRule() ? SWT.SINGLE | SWT.BORDER : SWT.READ_ONLY | SWT.BORDER;
        final Text nameField = new Text(parent, style);
        nameField.setFocus();

        Listener validateListener = new Listener() {
            public void handleEvent(Event event) {
                validateRuleParams();
                }
         	};

         nameField.addListener(SWT.Modify, validateListener);
         nameField.addListener(SWT.DefaultSelection, validateListener);

         return nameField;
     }

    // TODO move to RuleSet class
    public static final Comparator<RuleSet> byNameComparator = new Comparator<RuleSet>() {

    	public int compare(RuleSet rsA, RuleSet rsB) {
    		return rsA.getName().compareTo(rsB.getName());
    	};
    };

     private Combo buildRuleSetNameField(Composite parent) {

    	 int style = creatingNewRule() ? SWT.BORDER : SWT.READ_ONLY;
         Combo field = new Combo(parent, style);

         Set<RuleSet> rs = PMDPlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
         RuleSet[] ruleSets = rs.toArray(new RuleSet[rs.size()]);
         Arrays.sort(ruleSets, byNameComparator);
         for (RuleSet ruleSet : ruleSets) {
        	 field.add(ruleSet.getName());
         }

         Listener validateListener = new Listener() {
             public void handleEvent(Event event) {
                 validateRuleParams();
                 }
          	};

         field.addListener(SWT.Modify, validateListener);
         field.addListener(SWT.DefaultSelection, validateListener);

         return field;
     }

     private void implementationType(ImplementationType type) {

    	 switch (type) {
	    	 case XPath: {
	    		 implementationClassField.setEnabled(false);
	             usesTypeResolutionButton.setEnabled(false);
	             usesTypeResolutionButton.setSelection(true);
	             usesDfaButton.setEnabled(false);
	             usesDfaButton.setSelection(false);
	             implementationTypeCombo.select(0);
	             if (creatingNewRule()) {
	            	 implementationClassField.setType(XPathRule.class);
	             }
	    		 break;
	    	 }
	    	 case Java: {
	    		 implementationClassField.setEnabled(true);
	             usesTypeResolutionButton.setEnabled(true);
	             usesTypeResolutionButton.setSelection(true);
	             usesDfaButton.setEnabled(true);
	             usesDfaButton.setSelection(false);
	             implementationTypeCombo.select(1);
	             if (creatingNewRule()) {
	            	 implementationClassField.setType(null);
	             }
	    		 break;
	    	 }

	    	 case Mixed: {
	    		 implementationTypeCombo.deselectAll();
	    	 }
    	 }
    	 validateRuleParams();
     }

     private Combo buildImplementationTypeCombo(Composite parent) {

         final Combo combo = new Combo(parent, SWT.READ_ONLY);
     	 combo.add("XPath script");
     	 combo.add("Java class");

         combo.addSelectionListener(new SelectionAdapter() {
             @Override
             public void widgetSelected(SelectionEvent event) {
            	 int idx = combo.getSelectionIndex();
            	 switch (idx) {
            	 case 0:  { implementationType(ImplementationType.XPath); break; }
            	 case 1:  { implementationType(ImplementationType.Java);  break; }
            	 case -1: { implementationType(ImplementationType.Mixed); break; }
            	 }
             }
         });

     	combo.select(0);

        return combo;
     }
     
	private Combo buildLanguageCombo(Composite parent) {

		final List<Language> languages = Language.findWithRuleSupport();

		final Combo combo = new Combo(parent, SWT.READ_ONLY);

		Language deflt = Language.getDefaultLanguage();
		int selectionIndex = -1;

		for (int i = 0; i < languages.size(); i++) {
			if (languages.get(i) == deflt) selectionIndex = i;
			combo.add(languages.get(i).getName());
		}
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (rules == null) return;
				Language language = languages.get(combo.getSelectionIndex());
				rules.setLanguage(language);
				updateLanguageVersionComboSelections(language);
				changed(null, language.getName());
			}
		});

		combo.select(selectionIndex);

		return combo;
	}

     private void updateLanguageVersionComboSelections(Language language) {

    	 List<LanguageVersion> versions = language.getVersions();

    	 if (versions.size() > 1) {
    		 showLanguageVersionFields(language);
	    	 show(minLanguageVersionCombo, commonLanguageMinVersionName());
	    	 show(maxLanguageVersionCombo, commonLanguageMaxVersionName());
    	 } else {
    		 showLanguageVersionFields(null);
    	 }
     }

     private Language selectedLanguage() {
    	 int index = languageCombo.getSelectionIndex();
    	 if (index < 0) return null;	// should never happen!
    	 return Language.findWithRuleSupport().get(index);
     }

     private LanguageVersion selectedVersionIn(Combo versionCombo) {
    	 int index = versionCombo.getSelectionIndex();
    	 if (index < 0) return null;
    	 return selectedLanguage().getVersions().get(index);
     }

     private Combo buildLanguageVersionCombo(Composite parent, final boolean isMinVersion) {

    	 int style = creatingNewRule() ? SWT.SINGLE | SWT.BORDER : SWT.READ_ONLY | SWT.BORDER;
         final Combo combo = new Combo(parent, style);

         combo.addSelectionListener(new SelectionAdapter() {
 			public void widgetSelected(SelectionEvent event) {
 				if (rules == null) return;

 				final LanguageVersion version = selectedLanguage().getVersions().get(combo.getSelectionIndex());

 				RuleVisitor visitor = new RuleVisitor() {
 					public boolean accept(Rule rule) {
 						if (isMinVersion) {
 							rule.setMinimumLanguageVersion(version);
 						} else {
 							rule.setMaximumLanguageVersion(version);
 						}
 						return true;
 					}
 				};

 				rules.rulesDo(visitor);

 				valueChanged(null, version.getName());
 			}
 		});

         return combo;
     }


     private Combo buildPriorityCombo(Composite parent) {

        final Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
     //   combo.setEditable(false);
     	final RulePriority[] priorities = RulePriority.values();

     	for (RulePriority rulePriority : priorities) {
     		combo.add(UISettings.labelFor(rulePriority));
     	}

     	if (rules != null) {
	     	RulePriority priority = RuleUtil.commonPriority(rules);
	     	int index = priority == null ? -1 : priority.getPriority() - 1;
	     	combo.select(index);
     	}

     	combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setPriority(priorities[ combo.getSelectionIndex() ]);
				validateRuleParams();
			}
		});

        return combo;
     }

     private void setPriority(RulePriority priority) {
    	 priorityDisplay.setItems(new Object[] {priority});
    	 if (rules != null) rules.setPriority(priority);
    	 valueChanged(null, priority);
     }
     
     private Button buildUsesTypeResolutionButton(Composite parent) {

         final Button button = new Button(parent, SWT.CHECK);
         button.setText(SWTUtil.stringFor(StringKeys.PREF_RULEEDIT_BUTTON_USES_TYPE_RESOLUTION));
         return button;
     }

     private Button buildUsesDfaButton(Composite parent) {

         final Button button = new Button(parent, SWT.CHECK);
         button.setText(SWTUtil.stringFor(StringKeys.PREF_RULEEDIT_BUTTON_USES_DFA));
         return button;
     }

     private boolean hasValidRuleType() {
    	 
    	 if (!implementationClassField.isEnabled()) return true;
    	 
     	 Class<?> newType = implementationClassField.getType(false);
         return newType != null && Rule.class.isAssignableFrom(newType);
     }

     private String nameFieldValue() {
    	 return nameField.getText().trim();
     }
     
     private boolean hasValidRuleName() {

    	 if (creatingNewRule() && !isValidRuleName(nameFieldValue())) return false;   

    	 if (rules == null || rules.hasMultipleRules()) return true;

    	 return isValidRuleName(nameFieldValue());
     }
     
     private boolean hasExistingRuleName() {
    	 if (currentRuleNames == null) currentRuleNames = MarkerUtil.currentRuleNames();
    	 return currentRuleNames.contains(nameFieldValue());
     }

     private boolean hasValidRulesetName() {
     	String name = ruleSetNameField.getText();
     	return isValidRulesetName(name);
     }

     private static boolean hasNoSelection(Combo combo) {
    	 return combo.getSelectionIndex() < 0;
     }
     
     private boolean hasValidChoice(Combo combo) {
    	 
    	 if (creatingNewRule() && hasNoSelection(combo)) return false;    	 
    	 if (rules == null || rules.hasMultipleRules()) return true;
    	 return priorityCombo.getSelectionIndex() >= 0;
     }
     
     protected List<String> fieldErrors() {
    	 
    	 List<String> errors = new ArrayList<String>();
    	 
    	 if (!hasValidRuleType()) errors.add("Invalid rule class");
    	 if (!hasValidRuleName()) errors.add("Invalid rule name");
    	 if (creatingNewRule() && hasExistingRuleName()) errors.add("Rule name is already in use");
    	 if (!hasValidRulesetName()) errors.add("Invalid ruleset name");
    	 if (!hasValidChoice(priorityCombo)) errors.add("No priority selected");
    	 if (!hasValidChoice(languageCombo)) errors.add("No language selected");
    	 
    	 return errors;
     }
     
     private void validateRuleParams() {

     	boolean isOk = validate();

     	if (isOk && creatingNewRule()) {
     		populateRuleInstance();
     	}

     	if (inSetup) return;
     	
     	if (target != null) {
     		target.rule(
     				isOk ? 
     					rules.soleRule() : 
     					null
     					);
     	}
     }

     private void copyLocalValuesTo(Rule rule) {

     	rule.setName(nameFieldValue());
     	rule.setRuleSetName(ruleSetNameField.getText());

     	Language language = selectedLanguage();
     	rule.setLanguage(language);

     	rule.setPriority(
     			RulePriority.valueOf(priorityCombo.getSelectionIndex()+1)
     			);
         if (usesTypeResolutionButton.getSelection()) {
         	rule.setUsesTypeResolution();
         }
         if (usesDfaButton.getSelection()) {
         	rule.setUsesDFA();
         }

         rule.setMinimumLanguageVersion(selectedVersionIn(minLanguageVersionCombo));
         rule.setMaximumLanguageVersion(selectedVersionIn(maxLanguageVersionCombo));
     }

     private void populateRuleInstance() {

     	Class<Rule> ruleType = (Class<Rule>)implementationClassField.getType(true);

     	try {
     		Rule newRule = ruleType.newInstance();

     		if (rules == null) {
     			rules = new RuleSelection(newRule);
     		} else {
     			if (newRule.getClass() != soleRule().getClass()) {
     				rules.soleRule(newRule);
     			}
     		}

     		copyLocalValuesTo(rules.soleRule());

 		} catch (InstantiationException e) {
 			e.printStackTrace();
 		} catch (IllegalAccessException e) {
 			e.printStackTrace();
 		}
     }

 	private TypeText buildImplementationClassField(Composite parent) {

    	int style = creatingNewRule() ? SWT.SINGLE | SWT.BORDER : SWT.READ_ONLY | SWT.BORDER;
 		final TypeText classField = new TypeText(parent, style, true, "");

 	    classField.setEnabled(false);

         Listener validateListener = new Listener() {
             public void handleEvent(Event event) {
                validateRuleParams();
                }
          	};

       	classField.addListener(SWT.FocusOut, validateListener);
        classField.addListener(SWT.DefaultSelection, validateListener);

 	    return classField;
 	}

 	private static boolean isValidRuleName(String candidateName) {

 		if (StringUtil.isEmpty(candidateName)) return false;
 		// TODO

 		return true;
 	}

 	private static boolean isValidRulesetName(String candidateName) {

 		if (StringUtil.isEmpty(candidateName)) return false;
 		// TODO

 		return true;
 	}
}
