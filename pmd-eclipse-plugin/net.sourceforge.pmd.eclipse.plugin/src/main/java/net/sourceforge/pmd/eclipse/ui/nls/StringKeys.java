/*
 * Created on 18 mai 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.nls;


/**
 * Convenient class to hold PMD Constants
 * @author phherlin
 * @author bremedios
 */
public class StringKeys {
	
    public static final String PROPERTY_BUTTON_ENABLE = "property.button.enable";
    public static final String PROPERTY_LABEL_SELECT_RULE = "property.label.select_rule";
    public static final String PROPERTY_BUTTON_SELECT_WORKINGSET = "property.button.select_workingset";
    public static final String PROPERTY_BUTTON_DESELECT_WORKINGSET = "property.button.deselect_workingset";
    public static final String PROPERTY_LABEL_NO_WORKINGSET = "property.label.no_workingset";
    public static final String PROPERTY_LABEL_SELECTED_WORKINGSET = "property.label.selected_workingset";
    public static final String PROPERTY_BUTTON_STORE_RULESET_PROJECT = "property.button.store_ruleset_project";
    public static final String PROPERTY_BUTTON_RULESET_BROWSE = "property.button.ruleset_browse";
    public static final String PROPERTY_BUTTON_INCLUDE_DERIVED_FILES = "property.button.include_derived_files";
    public static final String PROPERTY_BUTTON_RUN_AT_FULL_BUILD = "property.button.full_build_enabled";
    public static final String PROPERTY_BUTTON_VIOLATIONS_AS_ERRORS = "property.button.violations_as_errors";
    
    public static final String PREF_GENERAL_HEADER = "preference.pmd.header";
    public static final String PREF_GENERAL_TITLE = "preference.pmd.title";
    public static final String PREF_GENERAL_LABEL_ADDCOMMENT = "preference.pmd.label.addcomment";
    public static final String PREF_GENERAL_LABEL_SAMPLE = "preference.pmd.label.sample";
    public static final String PREF_GENERAL_TOOLTIP_ADDCOMMENT = "preference.pmd.tooltip.addcomment";
    public static final String PREF_GENERAL_MESSAGE_INCORRECT_FORMAT ="preference.pmd.message.incorrect_format";
    public static final String PREF_GENERAL_GROUP_REVIEW = "preference.pmd.group.review";
    public static final String PREF_GENERAL_GROUP_PRIORITIES = "preference.pmd.group.priorities";
    public static final String PREF_GENERAL_GROUP_GENERAL = "preference.pmd.group.general";
    public static final String PREF_GENERAL_LABEL_SHOW_PERSPECTIVE = "preference.pmd.label.perspective_on_check";
    public static final String PREF_GENERAL_LABEL_USE_DFA = "preference.pmd.label.use_dfa";
    public static final String PREF_GENERAL_LABEL_USE_PROJECT_BUILD_PATH = "preference.pmd.label.use_project_build_path";
    public static final String PREF_GENERAL_LABEL_MAX_VIOLATIONS_PFPR = "preference.pmd.label.max_violations_pfpr";
    public static final String PREF_GENERAL_TOOLTIP_MAX_VIOLATIONS_PFPR = "preference.pmd.tooltip.max_violations_pfpr";
    public static final String PREF_GENERAL_MESSAGE_INVALID_NUMERIC_VALUE ="preference.pmd.message.invalid_numeric_value";
    public static final String PREF_GENERAL_REVIEW_PMD_STYLE = "preference.pmd.label.review_pmd_style";
    public static final String PREF_GENERAL_GROUP_LOGGING = "preference.pmd.group.logging";
    public static final String PREF_GENERAL_LABEL_LOG_FILE_NAME = "preference.pmd.label.log_file_name";
    public static final String PREF_GENERAL_TOOLTIP_LOG_FILE_NAME = "preference.pmd.tooltip.log_file_name";
    public static final String PREF_GENERAL_BUTTON_BROWSE = "preference.pmd.button.browse";
    public static final String PREF_GENERAL_DIALOG_BROWSE = "preference.pmd.dialog.browse";
    public static final String PREF_GENERAL_LABEL_LOG_LEVEL = "preference.pmd.label.log_level";
    
    //  TOOLTIP keys are not shown here...just append  ".tooltip" to an existing key to see if you have one :)
    public static final String PREF_RULESET_TITLE = "preference.ruleset.title";
    public static final String PREF_RULESET_LIST = "preference.ruleset.list";
    public static final String PREF_RULESET_ADD = "preference.ruleset.add";
    public static final String PREF_RULESET_REMOVE = "preference.ruleset.remove";
    public static final String PREF_RULESET_LABEL_RULETABLE = "preference.ruleset.label.ruletable";
    public static final String PREF_RULESET_LABEL_RULEPROPSTABLE = "preference.ruleset.label.rulepropstable";
    public static final String PREF_RULESET_LABEL_EXCLUDE_PATTERNS_TABLE = "preference.ruleset.label.exclude_patterns_table";
    public static final String PREF_RULESET_LABEL_INCLUDE_PATTERNS_TABLE = "preference.ruleset.label.include_patterns_table";
    public static final String PREF_RULESET_COLUMN_DATAFLOW = "preference.ruleset.column.dataflow";
    public static final String PREF_RULESET_COLUMN_LANGUAGE = "preference.ruleset.column.language";
    public static final String PREF_RULESET_COLUMN_RULESET = "preference.ruleset.column.ruleset";
    public static final String PREF_RULESET_COLUMN_RULESET_NAME = "preference.ruleset.column.ruleset_name";
    public static final String PREF_RULESET_COLUMN_RULE_NAME = "preference.ruleset.column.rule_name";
    public static final String PREF_RULESET_COLUMN_RULE_TYPE = "preference.ruleset.column.rule_type";
    public static final String PREF_RULESET_COLUMN_EXAMPLE_CNT = "preference.ruleset.column.example_count";
    public static final String PREF_RULESET_COLUMN_SINCE = "preference.ruleset.column.since";
    public static final String PREF_RULESET_COLUMN_FILTERS_REGEX = "preference.ruleset.column.filters.regex";
    public static final String PREF_RULESET_COLUMN_FILTERS_XPATH = "preference.ruleset.column.filters.xpath";
    public static final String PREF_RULESET_COLUMN_MIN_VER = "preference.ruleset.column.minimum_version";
    public static final String PREF_RULESET_COLUMN_MAX_VER = "preference.ruleset.column.maximum_version";
    public static final String PREF_RULESET_COLUMN_PRIORITY = "preference.ruleset.column.priority";
    public static final String PREF_RULESET_COLUMN_FIXCOUNT = "preference.ruleset.column.fixCount";
    public static final String PREF_RULESET_COLUMN_MODCOUNT = "preference.ruleset.column.modCount";
    public static final String PREF_RULESET_COLUMN_PROPERTIES = "preference.ruleset.column.properties";
    public static final String PREF_RULESET_COLUMN_DESCRIPTION = "preference.ruleset.column.description";
    public static final String PREF_RULESET_COLUMN_PROPERTY = "preference.ruleset.column.property";
    public static final String PREF_RULESET_COLUMN_VALUE = "preference.ruleset.column.value";
    public static final String PREF_RULESET_COLUMN_URL = "preference.ruleset.column.url";
    public static final String PREF_RULESET_COLUMN_EXCLUDE_PATTERN = "preference.ruleset.column.exclude_pattern";
    public static final String PREF_RULESET_COLUMN_INCLUDE_PATTERN = "preference.ruleset.column.include_pattern";
    public static final String PREF_RULESET_GROUPING_NONE = "preference.ruleset.grouping.none";
    public static final String PREF_RULESET_GROUPING_PMD_VERSION = "preference.ruleset.grouping.pmd_version";
    public static final String PREF_RULESET_GROUPING_REGEX = "preference.ruleset.grouping.regex";
    public static final String PREF_RULESET_BUTTON_ADDFILTER = "preference.ruleset.button.addfilter";
    public static final String PREF_RULESET_BUTTON_REMOVEFILTER = "preference.ruleset.button.removefilter";
    public static final String PREF_RULESET_BUTTON_ADDRULE = "preference.ruleset.button.addrule";
    public static final String PREF_RULESET_BUTTON_REMOVERULE = "preference.ruleset.button.removerule";
    public static final String PREF_RULESET_BUTTON_EDITRULE = "preference.ruleset.button.editrule";
    public static final String PREF_RULESET_BUTTON_IMPORTRULESET = "preference.ruleset.button.importruleset";
    public static final String PREF_RULESET_BUTTON_EXPORTRULESET = "preference.ruleset.button.exportruleset";
    public static final String PREF_RULESET_BUTTON_CLEARALL = "preference.ruleset.button.clearall";
    public static final String PREF_RULESET_BUTTON_RULEDESIGNER = "preference.ruleset.button.ruledesigner";
    public static final String PREF_RULESET_BUTTON_ADDPROPERTY = "preference.ruleset.button.addproperty";
    public static final String PREF_RULESET_BUTTON_ADD_EXCLUDE_PATTERN = "preference.ruleset.button.add_exclude_pattern";
    public static final String PREF_RULESET_BUTTON_ADD_INCLUDE_PATTERN = "preference.ruleset.button.add_include_pattern";
    public static final String PREF_RULESET_DIALOG_TITLE = "preference.ruleset.dialog.title";
    public static final String PREF_RULESET_DIALOG_RULESET_DESCRIPTION = "preference.ruleset.dialog.ruleset_description";
    public static final String PREF_RULESET_DIALOG_PROPERTY_NAME = "preference.ruleset.dialog.property_name";

    public static final String PREF_RULESET_RULES_GROUPED_BY = "preference.ruleset.label.rules_grouped_by";
    public static final String PREF_RULESET_ACTIVE_RULE_COUNT = "preference.ruleset.label.active_rule_count";

    public static final String PREF_RULESET_BUTTON_CHECK_ALL = "preference.ruleset.button.tooltip.check.all";
    public static final String PREF_RULESET_BUTTON_UNCHECK_ALL = "preference.ruleset.button.tooltip.uncheck.all";

    public static final String PREF_RULESET_TAB_RULE = "preference.ruleedit.tab.rule";
    public static final String PREF_RULESET_TAB_PROPERTIES = "preference.ruleedit.tab.properties";
    public static final String PREF_RULESET_TAB_DESCRIPTION = "preference.ruleedit.tab.description";
    public static final String PREF_RULESET_TAB_EXCLUSIONS = "preference.ruleedit.tab.exclusions";
    public static final String PREF_RULESET_TAB_XPATH = "preference.ruleedit.tab.xpath";
    public static final String PREF_RULESET_TAB_FIXES = "preference.ruleedit.tab.fixes";
    public static final String PREF_RULESET_TAB_EXAMPLES = "preference.ruleedit.tab.examples";
    public static final String PREF_RULESET_TAB_FULLVIEW = "preference.ruleedit.tab.fullview";

    public static final String LABEL_XPATH_EXCLUSION = "preference.ruleedit.label.xpath_exclusion";
    public static final String LABEL_EXCLUSION_REGEX = "preference.ruleedit.label.exclusion_regex";
    public static final String LABEL_COLOUR_CODE = "preference.ruleedit.label.colour_code";
    public static final String LABEL_GROUP_BY = "preference.groupby";
    
    public static final String PREF_RULESETSELECTION_LABEL_ENTER_RULESET = "preference.rulesetselection.label.enter_ruleset";
    public static final String PREF_RULESETSELECTION_TOOLTIP_RULESET = "preference.rulesetselection.tooltip.ruleset";
    public static final String PREF_RULESETSELECTION_BUTTON_BROWSE = "preference.rulesetselection.button.browse";
    public static final String PREF_RULESETSELECTION_BUTTON_REFERENCE = "preference.rulesetselection.button.reference";
    public static final String PREF_RULESETSELECTION_BUTTON_COPY = "preference.rulesetselection.button.copy";
    
    public static final String PREF_RULEEDIT_LABEL_RULESET_NAME = "preference.ruleedit.label.ruleset_name";
    public static final String PREF_RULEEDIT_LABEL_SINCE = "preference.ruleedit.label.since";
    public static final String PREF_RULEEDIT_LABEL_NAME = "preference.ruleedit.label.name";
    public static final String PREF_RULEEDIT_LABEL_IMPLEMENTATION_CLASS = "preference.ruleedit.label.implementation_class";
    public static final String PREF_RULEEDIT_LABEL_IMPLEMENTED_BY = "preference.ruleedit.label.implemented_by";
    public static final String PREF_RULEEDIT_LABEL_MESSAGE = "preference.ruleedit.label.message";
    public static final String PREF_RULEEDIT_LABEL_LANGUAGE = "preference.ruleedit.label.language";
    public static final String PREF_RULEEDIT_LABEL_LANGUAGE_MIN = "preference.ruleedit.label.language.version.min";
    public static final String PREF_RULEEDIT_LABEL_LANGUAGE_MAX = "preference.ruleedit.label.language.version.max";
    public static final String PREF_RULEEDIT_LABEL_PRIORITY = "preference.ruleedit.label.priority";
    public static final String PREF_RULEEDIT_LABEL_DESCRIPTION = "preference.ruleedit.label.description";
    public static final String PREF_RULEEDIT_LABEL_EXTERNAL_INFO_URL = "preference.ruleedit.label.external_info_url";
    public static final String PREF_RULEEDIT_LABEL_EXAMPLES = "preference.ruleedit.label.examples";
    public static final String PREF_RULEEDIT_LABEL_XPATH = "preference.ruleedit.label.xpath";
    public static final String PREF_RULEEDIT_LABEL_XPATH_VERSION = "preference.ruleedit.label.xpath.version";
    public static final String PREF_RULEEDIT_BUTTON_RULE_REFERENCE = "preference.ruleedit.button.rule_reference";
    public static final String PREF_RULEEDIT_BUTTON_XPATH_RULE = "preference.ruleedit.button.xpath_rule";
    public static final String PREF_RULEEDIT_BUTTON_USES_TYPE_RESOLUTION = "preference.ruleedit.button.uses_type_resolution";
    public static final String PREF_RULEEDIT_BUTTON_USES_DFA = "preference.ruleedit.button.uses_dfa";
    public static final String PREF_RULEEDIT_BUTTON_OPEN_EXTERNAL_INFO_URL = "preference.ruleedit.button.open_external_info_url";
    
    public static final String PREF_CPD_GROUP_GENERAL = "preference.cpd.group.general";
    public static final String PREF_CPD_TITLE = "preference.cpd.title";
    public static final String PREF_CPD_TILESIZE = "preference.cpd.tilesize";

    public static final String PREF_SUMMARY_LABEL_NAME = "preference.summary.label.name";
    public static final String PREF_SUMMARY_LABEL_DESCRIPTION = "preference.summary.label.description";
    public static final String PREF_SUMMARY_LABEL_PARAMETERS = "preference.summary.label.parameters";
    public static final String PREF_SUMMARY_LABEL_EXAMPLE = "preference.summary.label.example";
    
    public static final String VIEW_OUTLINE_DEFAULT_TEXT = "view.outline.default_text";
    public static final String VIEW_OUTLINE_COLUMN_MESSAGE = "view.outline.column_message";
    public static final String VIEW_OUTLINE_COLUMN_LINE = "view.outline.column_line";
    public static final String VIEW_OVERVIEW_COLUMN_ELEMENT = "view.overview.column_element";
    public static final String VIEW_OVERVIEW_COLUMN_VIO_TOTAL = "view.overview.column_vio_total";
    public static final String VIEW_OVERVIEW_COLUMN_VIO_KLOC = "view.overview.column_vio_loc";
    public static final String VIEW_OVERVIEW_COLUMN_VIO_METHOD = "view.overview.column_vio_method";
    public static final String VIEW_OVERVIEW_COLUMN_PROJECT = "view.overview.column_project";
    public static final String VIEW_DATAFLOW_DEFAULT_TEXT = "view.dataflow.default_text";
    public static final String VIEW_DATAFLOW_CHOOSE_METHOD = "view.dataflow.choose_method";
    public static final String VIEW_DATAFLOW_GRAPH_COLUMN_LINE = "view.dataflow.graph.column_line";
    public static final String VIEW_DATAFLOW_GRAPH_COLUMN_GRAPH = "view.dataflow.graph.column_graph";
    public static final String VIEW_DATAFLOW_GRAPH_COLUMN_NEXT = "view.dataflow.graph.column_nextnodes";
    public static final String VIEW_DATAFLOW_GRAPH_COLUMN_VALUES = "view.dataflow.graph.column_values";
    public static final String VIEW_DATAFLOW_GRAPH_COLUMN_CODE = "view.dataflow.graph.column_code";
    public static final String VIEW_DATAFLOW_SWITCHBUTTON_SHOW = "view.dataflow.switchbutton.show";
    public static final String VIEW_DATAFLOW_SWITCHBUTTON_HIDE = "view.dataflow.switchbutton.hide";
    public static final String VIEW_DATAFLOW_REFRESHBUTTON = "view.dataflow.refreshbutton";
    public static final String VIEW_DATAFLOW_TABLE_COLUMN_TYPE = "view.dataflow.table.column_type";
    public static final String VIEW_DATAFLOW_TABLE_COLUMN_LINE = "view.dataflow.table.column_line";
    public static final String VIEW_DATAFLOW_TABLE_COLUMN_VARIABLE = "view.dataflow.table.column_variable";
    public static final String VIEW_DATAFLOW_TABLE_COLUMN_METHOD = "view.dataflow.table.column_method";
    public static final String VIEW_DATAFLOW_TABLE_COLUMN_TYPE_TOOLTIP = "view.dataflow.table.column_type.tooltip";
    
    public static final String VIEW_AST_DEFAULT_TEXT = "view.ast.default_text";
    
    public static final String VIEW_FILTER_PRIORITY = "view.filter.priority";
    public static final String VIEW_FILTER_PRIORITY_1 = "view.filter.priority.1";
    public static final String VIEW_FILTER_PRIORITY_2 = "view.filter.priority.2";
    public static final String VIEW_FILTER_PRIORITY_3 = "view.filter.priority.3";
    public static final String VIEW_FILTER_PRIORITY_4 = "view.filter.priority.4";
    public static final String VIEW_FILTER_PRIORITY_5 = "view.filter.priority.5";
    public static final String VIEW_FILTER_PROJECT_PREFIX = "view.filter.project_prefix";
    
    public static final String VIEW_ACTION_CURRENT_PROJECT = "view.action.current_project";
    
    public static final String VIEW_TOOLTIP_FILTER_PRIORITY = "view.tooltip.filter.priority";
    public static final String VIEW_TOOLTIP_FILTER_PRIORITY_1 = "view.tooltip.filter.priority.1";
    public static final String VIEW_TOOLTIP_FILTER_PRIORITY_2 = "view.tooltip.filter.priority.2";
    public static final String VIEW_TOOLTIP_FILTER_PRIORITY_3 = "view.tooltip.filter.priority.3";
    public static final String VIEW_TOOLTIP_FILTER_PRIORITY_4 = "view.tooltip.filter.priority.4";
    public static final String VIEW_TOOLTIP_FILTER_PRIORITY_5 = "view.tooltip.filter.priority.5";
    public static final String VIEW_TOOLTIP_PACKAGES_FILES = "view.tooltip.packages_files";
    public static final String VIEW_TOOLTIP_COLLAPSE_ALL = "view.tooltip.collapse_all";
    public static final String VIEW_TOOLTIP_EXPAND_ALL = "view.tooltip.expand_all";
        
    public static final String VIEW_COLUMN_MESSAGE = "view.column.message";
    public static final String VIEW_COLUMN_RULE = "view.column.rule";
    public static final String VIEW_COLUMN_CLASS = "view.column.class";
    public static final String VIEW_COLUMN_PACKAGE = "view.column.package";
    public static final String VIEW_COLUMN_PROJECT = "view.column.project";
    public static final String VIEW_COLUMN_LOCATION = "view.column.location";
    public static final String VIEW_TOOLTIP_PROJECT = "view.tooltip.project";
    public static final String VIEW_TOOLTIP_FILE = "view.tooltip.file";
    public static final String VIEW_TOOLTIP_DISABLE = "view.tooltip.disable";
    public static final String VIEW_TOOLTIP_ERRORHIGH_FILTER = "view.tooltip.errorhigh_filter";
    public static final String VIEW_TOOLTIP_ERROR_FILTER = "view.tooltip.error_filter";
    public static final String VIEW_TOOLTIP_WARNINGHIGH_FILTER = "view.tooltip.warninghigh_filter";
    public static final String VIEW_TOOLTIP_WARNING_FILTER = "view.tooltip.warning_filter";
    public static final String VIEW_TOOLTIP_INFORMATION_FILTER = "view.tooltip.information_filter";
    public static final String VIEW_TOOLTIP_SHOW_RULE = "view.tooltip.show_rule";
    public static final String VIEW_TOOLTIP_REMOVE_VIOLATION = "view.tooltip.remove_violation";
    public static final String VIEW_TOOLTIP_REFRESH = "view.tooltip.refresh_resource";
    public static final String VIEW_TOOLTIP_REVIEW = "view.tooltip.review";
    public static final String VIEW_TOOLTIP_QUICKFIX = "view.tooltip.quickfix";
    public static final String VIEW_TOOLTIP_CALCULATE_STATS = "view.tooltip.calc_stats";
    public static final String VIEW_TOOLTIP_COMPUTE_METRICS = "view.tooltip.compute_metrics";
    public static final String VIEW_ACTION_PROJECT = "view.action.project";
    public static final String VIEW_ACTION_DISABLE_RULE = "view.action.disable.rule";
    public static final String VIEW_ACTION_FILE = "view.action.file";
    public static final String VIEW_ACTION_ERRORHIGH = "view.action.errorhigh";
    public static final String VIEW_ACTION_ERROR = "view.action.error";
    public static final String VIEW_ACTION_WARNINGHIGH = "view.action.warninghigh";
    public static final String VIEW_ACTION_WARNING = "view.action.warning";
    public static final String VIEW_ACTION_INFORMATION = "view.action.information";
    public static final String VIEW_ACTION_SHOW_RULE = "view.action.show_rule";
    public static final String VIEW_ACTION_REMOVE_VIOLATION = "view.action.remove_violation";
    public static final String VIEW_ACTION_REVIEW = "view.action.review";
    public static final String VIEW_ACTION_QUICKFIX = "view.action.quickfix";
    public static final String VIEW_MENU_RESOURCE_FILTER = "view.menu.resource_filter";
    public static final String VIEW_MENU_PRIORITY_FILTER = "view.menu.priority_filter";
    public static final String VIEW_DEFAULT_PACKAGE = "view.default_package";
    public static final String VIEW_MENU_FILEMARKERS = "view.menu.show_file_markers";
    public static final String VIEW_MENU_MARKERFILES = "view.menu.show_marker_files";
    public static final String VIEW_MENU_PACKFILES = "view.menu.show_pack_files";
    public static final String VIEW_MENU_PRESENTATION_TYPE = "view.menu.show_type";
    
    public static final String DIALOG_PREFS_ADD_NEW_PROPERTY = "dialog.preferences.add_new_property";
    
    public static final String RULEEDIT_LABEL_MIN = "preference.ruleedit.label.min";
    public static final String RULEEDIT_LABEL_MAX = "preference.ruleedit.label.max";
    public static final String RULEEDIT_LABEL_DEFAULT = "preference.ruleedit.label.default";
    
    public static final String DIALOG_CPD_TITLE = "dialog.cpd.title";
    public static final String DIALOG_CPD_MIN_TILESIZE_LABEL = "dialog.cpd.min_tilesize.label";
    public static final String DIALOG_CPD_CREATEREPORT = "dialog.cpd.create_report";
    public static final String DIALOG_CPD_REPORT = "dialog.cpd.report";
    public static final String DIALOG_CPD_FORMAT_LABEL = "dialog.cpd.format.label";
    public static final String DIALOG_CPD_LANGUAGE_LABEL = "dialog.cpd.language.label";
    public static final String DIALOG_TOOLTIP_CPD_FORMAT = "dialog.tooltip.cpd.format";
    public static final String DIALOG_TOOLTIP_CPD_MIN_TILESIZE = "dialog.tooltip.cpd.tilesize";
    public static final String DIALOG_TOOLTIP_CPD_LANGUAGE = "dialog.tooltip.cpd.language";
    public static final String DIALOG_CPD_HELP_LABEL = "dialog.cpd.help.label";
    
    public static final String DIALOG_CPD_NORESULTS_HEADER = "dialog.cpd.no_results.header";
    public static final String DIALOG_CPD_NORESULTS_BODY = "dialog.cpd.no_results.body";
    
    public static final String ERROR_TITLE = "message.error.title";
    public static final String ERROR_CORE_EXCEPTION = "message.error.core_exception";
    public static final String ERROR_PMD_EXCEPTION = "message.error.pmd_exception";
    public static final String ERROR_IO_EXCEPTION = "message.error.io_exception";
    public static final String ERROR_JAVAMODEL_EXCEPTION = "message.error.javamodel_exception";
    public static final String ERROR_INVOCATIONTARGET_EXCEPTION = "message.error.invocationtarget_exception";
    public static final String ERROR_INTERRUPTED_EXCEPTION = "message.error.interrupted_exception";
    public static final String ERROR_RUNTIME_EXCEPTION = "message.error.runtime_exception";    
    public static final String ERROR_RULESET_NOT_FOUND = "message.error.ruleset_not_found";
    public static final String ERROR_IMPORTING_RULESET = "message.error.importing_ruleset";
    public static final String ERROR_EXPORTING_RULESET = "message.error.exporting_ruleset";
    public static final String ERROR_READING_PREFERENCE = "message.error.reading_preference";
    public static final String ERROR_WRITING_PREFERENCE = "message.error.writing_preference";
    public static final String ERROR_STORING_PROPERTY = "message.error.storing_property";
    public static final String ERROR_FIND_MARKER = "message.error.find_marker";
    public static final String ERROR_LOADING_RULESET = "message.error.loading_ruleset";
    public static final String ERROR_VIEW_EXCEPTION = "message.error.view_exception";
    public static final String ERROR_FILE_NOT_FOUND = "message.error.file_not_found";
    public static final String ERROR_CREATING_REPORT = "message.error.creating_report";
    
    public static final String QUESTION_TITLE = "message.question.title";
    public static final String QUESTION_RULES_CHANGED = "message.question.rules_changed";
    public static final String QUESTION_REBUILD_PROJECT = "message.question.rebuild_project";
    public static final String QUESTION_CREATE_RULESET_FILE = "message.question.create_ruleset_file";
    
    public static final String CONFIRM_TITLE = "message.confirm.title";
    public static final String CONFIRM_RULESET_EXISTS = "message.confirm.ruleset_exists";
    public static final String CONFIRM_CLEAR_RULESET = "message.confirm.clear_ruleset";
    public static final String CONFIRM_REVIEW_MULTIPLE_MARKERS = "message.confirm.review_multiple_markers";
    
    public static final String INFORMATION_TITLE = "message.information.title";
    public static final String INFORMATION_RULESET_EXPORTED = "message.information.ruleset_exported";
    
    public static final String WARNING_TITLE = "message.warning.title";
    public static final String WARNING_NAME_MANDATORY = "message.warning.name_mandatory";
    public static final String WARNING_PRIORITY_MANDATORY = "message.warning.priority_mandatory";
    public static final String WARNING_MESSAGE_MANDATORY = "message.warning.message_mandatory";
    public static final String WARNING_XPATH_MANDATORY = "message.warning.xpath_mandatory";
    public static final String WARNING_CLASS_INVALID = "message.warning.class_invalid";

    // these aren't used in the modern UI...can be removed
    public static final String PRIORITY_ERROR_HIGH   = "priority.error_high";
    public static final String PRIORITY_ERROR        = "priority.error";
    public static final String PRIORITY_WARNING_HIGH = "priority.warning_high";
    public static final String PRIORITY_WARNING      = "priority.warning";
    public static final String PRIORITY_INFORMATION  = "priority.information";
    
    public static final String MONITOR_JOB_TITLE = "monitor.job_title";
    public static final String MONITOR_CHECKING_FILE = "monitor.checking_file";
    public static final String PMD_PROCESSING = "monitor.begintask";
    public static final String MONITOR_UPDATING_PROJECTS = "monitor.updating_projects";
    public static final String MONITOR_REVIEW = "monitor.review";
    public static final String MONITOR_REMOVE_REVIEWS = "monitor.remove_reviews";
    public static final String MONITOR_CALC_STATS_TASK = "monitor.calc_stats";
    public static final String MONITOR_CALC_STATS_OF_PACKAGE = "monitor.calc_stats.package";
    public static final String MSGKEY_MONITOR_COLLECTING_MARKERS = "monitor.collect_markers";
    
    public static final String PRIORITY_COLUMN_NAME = "priority.column.name";
    public static final String PRIORITY_COLUMN_PMD_NAME = "priority.column.name.pmd";
    public static final String PRIORITY_COLUMN_VALUE = "priority.column.value";
    public static final String PRIORITY_COLUMN_SIZE = "priority.column.size";
    public static final String PRIORITY_COLUMN_SYMBOL = "priority.column.symbol";
    public static final String PRIORITY_COLUMN_COLOR = "priority.column.color";
    public static final String PRIORITY_COLUMN_DESC = "priority.column.description";
    
    public static final String NODE_COLUMN_NAME = "node.column.name";
    public static final String NODE_IMAGE_DATA = "node.column.image_data";
    public static final String NODE_LINE_NUM = "node.column.line_num";
    public static final String NODE_DERIVED = "node.column.derived";    
    public static final String NODE_IMG_OR_DERIVED = "node.column.img_or_derived";
    
    /**
     * This class is not meant to be instantiated
     *
     */
    private StringKeys() {
        super();
    }
    
}
