package net.sourceforge.pmd.eclipse;

/**
 * Convenient class to hold PMD Constants
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 */
public interface PMDConstants {
    // Message keys
    public static final String MSGKEY_ENABLE_BUTTON_LABEL = "property.pmd.enable";
    public static final String MSGKEY_PMD_PROCESSING = "monitor.begintask";
    
    public static final String MSGKEY_PREF_GENERAL_TITLE = "preference.pmd.title";
    public static final String MSGKEY_PREF_RULESET_TITLE = "preference.ruleset.title";
    public static final String MSGKEY_PREF_RULESET_LIST = "preference.ruleset.list";
    public static final String MSGKEY_PREF_RULESET_ADD = "preference.ruleset.add";
    public static final String MSGKEY_PREF_RULESET_REMOVE = "preference.ruleset.remove";
    public static final String MSGKEY_PREF_RULESET_LABEL_RULETABLE = "preference.ruleset.label.ruletable";
    public static final String MSGKEY_PREF_RULESET_LABEL_RULEPROPSTABLE = "preference.ruleset.label.rulepropstable";
    public static final String MSGKEY_PREF_RULESET_COLUMN_NAME = "preference.ruleset.column.name";
    public static final String MSGKEY_PREF_RULESET_COLUMN_PRIORITY = "preference.ruleset.column.priority";
    public static final String MSGKEY_PREF_RULESET_COLUMN_DESCRIPTION = "preference.ruleset.column.description";
    public static final String MSGKEY_PREF_RULESET_COLUMN_PROPERTY = "preference.ruleset.column.property";
    public static final String MSGKEY_PREF_RULESET_COLUMN_VALUE = "preference.ruleset.column.value";
    public static final String MSGKEY_PREF_RULESET_BUTTON_ADDRULE = "preference.ruleset.button.addrule";
    public static final String MSGKEY_PREF_RULESET_BUTTON_REMOVERULE = "preference.ruleset.button.removerule";
    public static final String MSGKEY_PREF_RULESET_BUTTON_EDITRULE = "preference.ruleset.button.editrule";
    public static final String MSGKEY_PREF_RULESET_BUTTON_IMPORTRULESET = "preference.ruleset.button.importruleset";
    public static final String MSGKEY_PREF_RULESET_BUTTON_EXPORTRULESET = "preference.ruleset.button.exportruleset";
    public static final String MSGKEY_PREF_RULESET_BUTTON_CLEARALL = "preference.ruleset.button.clearall";
    public static final String MSGKEY_PREF_RULESET_BUTTON_ADDPROPERTY = "preference.ruleset.button.addproperty";
    public static final String MSGKEY_PREF_RULESET_DIALOG_TITLE = "preference.ruleset.dialog.title";
    public static final String MSGKEY_PREF_RULESET_DIALOG_RULESET_DESCRIPTION = "preference.ruleset.dialog.ruleset_description";
    public static final String MSGKEY_PREF_RULESET_DIALOG_PROPERTY_NAME = "preference.ruleset.dialog.property_name";
    
    public static final String MSGKEY_PREF_RULESETSELECTION_LABEL_ENTER_RULESET = "preference.rulesetselection.label.enter_ruleset";
    public static final String MSGKEY_PREF_RULESETSELECTION_TOOLTIP_RULESET = "preference.rulesetselection.tooltip.ruleset";
    public static final String MSGKEY_PREF_RULESETSELECTION_BUTTON_BROWSE = "preference.rulesetselection.button.browse";
    
    public static final String MSGKEY_PREF_RULEEDIT_LABEL_NAME = "preference.ruleedit.label.name";
    public static final String MSGKEY_PREF_RULEEDIT_LABEL_IMPLEMENTATION_CLASS = "preference.ruleedit.label.implementation_class";
    public static final String MSGKEY_PREF_RULEEDIT_LABEL_MESSAGE = "preference.ruleedit.label.message";
    public static final String MSGKEY_PREF_RULEEDIT_LABEL_DESCRIPTION = "preference.ruleedit.label.description";
    public static final String MSGKEY_PREF_RULEEDIT_LABEL_EXAMPLE = "preference.ruleedit.label.example";
    public static final String MSGKEY_PREF_RULEEDIT_BUTTON_XPATH_RULE = "preference.ruleedit.button.xpath_rule";

    public static final String MSGKEY_PREF_CPD_TITLE = "preference.cpd.title";
    public static final String MSGKEY_PREF_CPD_TILESIZE = "preference.cpd.tilesize";
    
    public static final String MSGKEY_ERROR_TITLE = "message.error.title";
    public static final String MSGKEY_ERROR_CORE_EXCEPTION = "message.error.core_exception";
    public static final String MSGKEY_ERROR_PMD_EXCEPTION = "message.error.pmd_exception";
    public static final String MSGKEY_ERROR_RULESET_NOT_FOUND = "message.error.ruleset_not_found";
    public static final String MSGKEY_ERROR_IMPORTING_RULESET = "message.error.importing_ruleset";
    public static final String MSGKEY_ERROR_EXPORTING_RULESET = "message.error.exporting_ruleset";
    public static final String MSGKEY_ERROR_READING_PREFERENCE = "message.error.reading_preference";
    public static final String MSGKEY_ERROR_WRITING_PREFERENCE = "message.error.writing_preference";
    
    public static final String MSGKEY_QUESTION_TITLE = "message.question.title";
    public static final String MSGKEY_QUESTION_RULES_CHANGED = "message.question.rules_changed";
    
    public static final String MSGKEY_CONFIRM_TITLE = "message.confirm.title";
    public static final String MSGKEY_CONFIRM_RULESET_EXISTS = "message.confirm.ruleset_exists";
    public static final String MSGKEY_CONFIRM_CLEAR_RULESET = "message.confirm.clear_ruleset";
    
    public static final String MSGKEY_INFORMATION_TITLE = "message.information.title";
    public static final String MSGKEY_INFORMATION_RULESET_EXPORTED = "message.information.ruleset_exported";
    
    public static final String MSGKEY_WARNING_TITLE = "message.warning.title";
    public static final String MSGKEY_WARNING_NAME_MANDATORY = "message.warning.name_mandatory";
    public static final String MSGKEY_WARNING_MESSAGE_MANDATORY = "message.warning.message_mandatory";
    public static final String MSGKEY_WARNING_CLASS_INVALID = "message.warning.class_invalid";
    
    public static final String MSGKEY_PRIORITY_ERROR_HIGH   = "priority.error_high";
    public static final String MSGKEY_PRIORITY_ERROR        = "priority.error";
    public static final String MSGKEY_PRIORITY_WARNING_HIGH = "priority.warning_high";
    public static final String MSGKEY_PRIORITY_WARNING      = "priority.warning";
    public static final String MSGKEY_PRIORITY_INFORMATION  = "priority.information";
    
}
