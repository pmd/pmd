package net.sourceforge.pmd.eclipse;

/**
 * Convenient class to hold PMD Constants
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2003/08/11 21:58:06  phherlin
 * Adding a label for the default package
 *
 * Revision 1.6  2003/07/07 19:23:59  phherlin
 * Adding PMD violations view
 *
 * Revision 1.5  2003/07/01 20:22:16  phherlin
 * Make rules selectable from projects
 *
 * Revision 1.4  2003/06/30 22:00:53  phherlin
 * Adding clearer monitor message when visiting files
 *
 * Revision 1.3  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 */
public interface PMDConstants {
    // Message keys
    public static final String MSGKEY_PROPERTY_BUTTON_ENABLE = "property.button.enable";
    public static final String MSGKEY_PROPERTY_LABEL_SELECT_RULE = "property.label.select_rule";
    
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
    
    public static final String MSGKEY_VIEW_COLUMN_MESSAGE = "view.column.message";
    public static final String MSGKEY_VIEW_COLUMN_RULE = "view.column.rule";
    public static final String MSGKEY_VIEW_COLUMN_CLASS = "view.column.class";
    public static final String MSGKEY_VIEW_COLUMN_PACKAGE = "view.column.package";
    public static final String MSGKEY_VIEW_COLUMN_PROJECT = "view.column.project";
    public static final String MSGKEY_VIEW_COLUMN_LOCATION = "view.column.location";
    public static final String MSGKEY_VIEW_TOOLTIP_PROJECT = "view.tooltip.project";
    public static final String MSGKEY_VIEW_TOOLTIP_FILE = "view.tooltip.file";
    public static final String MSGKEY_VIEW_TOOLTIP_ERRORHIGH_FILTER = "view.tooltip.errorhigh_filter";
    public static final String MSGKEY_VIEW_TOOLTIP_ERROR_FILTER = "view.tooltip.error_filter";
    public static final String MSGKEY_VIEW_TOOLTIP_WARNINGHIGH_FILTER = "view.tooltip.warninghigh_filter";
    public static final String MSGKEY_VIEW_TOOLTIP_WARNING_FILTER = "view.tooltip.warning_filter";
    public static final String MSGKEY_VIEW_TOOLTIP_INFORMATION_FILTER = "view.tooltip.information_filter";
    public static final String MSGKEY_VIEW_TOOLTIP_SHOW_RULE = "view.tooltip.show_rule";
    public static final String MSGKEY_VIEW_TOOLTIP_REMOVE_VIOLATION = "view.tooltip.remove_violation";
    public static final String MSGKEY_VIEW_ACTION_PROJECT = "view.action.project";
    public static final String MSGKEY_VIEW_ACTION_FILE = "view.action.file";
    public static final String MSGKEY_VIEW_ACTION_ERRORHIGH = "view.action.errorhigh";
    public static final String MSGKEY_VIEW_ACTION_ERROR = "view.action.error";
    public static final String MSGKEY_VIEW_ACTION_WARNINGHIGH = "view.action.warninghigh";
    public static final String MSGKEY_VIEW_ACTION_WARNING = "view.action.warning";
    public static final String MSGKEY_VIEW_ACTION_INFORMATION = "view.action.information";
    public static final String MSGKEY_VIEW_ACTION_SHOW_RULE = "view.action.show_rule";
    public static final String MSGKEY_VIEW_ACTION_REMOVE_VIOLATION = "view.action.remove_violation";
    public static final String MSGKEY_VIEW_MENU_RESOURCE_FILTER = "view.menu.resource_filter";
    public static final String MSGKEY_VIEW_MENU_PRIORITY_FILTER = "view.menu.priority_filter";
    public static final String MSGKEY_VIEW_DEFAULT_PACKAGE = "view.default_package";
    
    public static final String MSGKEY_ERROR_TITLE = "message.error.title";
    public static final String MSGKEY_ERROR_CORE_EXCEPTION = "message.error.core_exception";
    public static final String MSGKEY_ERROR_PMD_EXCEPTION = "message.error.pmd_exception";
    public static final String MSGKEY_ERROR_RULESET_NOT_FOUND = "message.error.ruleset_not_found";
    public static final String MSGKEY_ERROR_IMPORTING_RULESET = "message.error.importing_ruleset";
    public static final String MSGKEY_ERROR_EXPORTING_RULESET = "message.error.exporting_ruleset";
    public static final String MSGKEY_ERROR_READING_PREFERENCE = "message.error.reading_preference";
    public static final String MSGKEY_ERROR_WRITING_PREFERENCE = "message.error.writing_preference";
    public static final String MSGKEY_ERROR_STORING_PROPERTY = "message.error.storing_property";
    public static final String MSGKEY_ERROR_FIND_MARKER = "message.error.find_marker";
    
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
    
    public static final String MSGKEY_MONITOR_CHECKING_FILE = "monitor.checking_file";
    public static final String MSGKEY_PMD_PROCESSING = "monitor.begintask";
    public static final String MSGKEY_MONITOR_UPDATING_PROJECTS = "monitor.updating_projects";
    
}
