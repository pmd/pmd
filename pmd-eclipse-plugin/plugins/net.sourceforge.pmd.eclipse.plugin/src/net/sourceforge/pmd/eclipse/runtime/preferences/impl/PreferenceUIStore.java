package net.sourceforge.pmd.eclipse.runtime.preferences.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.TextColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PreferenceStore;

/**
 * 
 *  
 * @author Brian Remedios
 */
public class PreferenceUIStore {

	private PreferenceStore preferenceStore;
		
	private static final String tableFraction 		= PMDPlugin.PLUGIN_ID + ".ruletable.fraction";
	private static final String tableHiddenCols 	= PMDPlugin.PLUGIN_ID + ".ruletable.hiddenColumns";
	private static final String tableColumnSortUp 	= PMDPlugin.PLUGIN_ID + ".ruletable.sortUp";
	private static final String groupingColumn 		= PMDPlugin.PLUGIN_ID + ".ruletable.groupingColumn";
	private static final String selectedRuleNames	= PMDPlugin.PLUGIN_ID + ".ruletable.selectedRules";
	private static final String selectedPropertyTab	= PMDPlugin.PLUGIN_ID + ".ruletable.selectedPropertyTab";
	
	private static final int tableFractionDefault = 55;
	private static final char stringSeparator = ',';
	
	private static final RuleColumnDescriptor[] defaultHiddenColumns = new RuleColumnDescriptor[] {
			TextColumnDescriptor.externalURL,		TextColumnDescriptor.minLangVers, TextColumnDescriptor.fixCount,
			TextColumnDescriptor.exampleCount,		TextColumnDescriptor.maxLangVers, TextColumnDescriptor.since,
			TextColumnDescriptor.modCount
			};

	private static final boolean defaultSortUp = false;
		
	public static final PreferenceUIStore instance = new PreferenceUIStore();
	
	private static String defaultHiddenColumnNames() {
		 Set<String> colNames = new HashSet<String>(defaultHiddenColumns.length);
		 for (RuleColumnDescriptor rcDesc : defaultHiddenColumns) {
			 colNames.add(rcDesc.label());
		 }
		 return SWTUtil.asString(colNames, stringSeparator);
	}
	
	private PreferenceUIStore() { 
		initialize();
	}
		
	private void initialize() {
		
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IPath path = root.getLocation();
        String fileName = path.append(PreferencesManagerImpl.NEW_PREFERENCE_LOCATION).toString();
				
	//	TODO - replace this with the existing ViewMemento 
		preferenceStore = new PreferenceStore(fileName);

	    try {
			preferenceStore.load();
		} catch (IOException e) {
			createNewStore();
		}
	}
	
	private void createNewStore() {
		
		 preferenceStore.setValue(tableFraction, tableFractionDefault);		 
		 preferenceStore.setValue(tableHiddenCols, defaultHiddenColumnNames());
		 preferenceStore.setValue(tableColumnSortUp, defaultSortUp);
		 preferenceStore.setValue(groupingColumn, "");
		 preferenceStore.setValue(selectedRuleNames, "");
		 preferenceStore.setValue(selectedPropertyTab, 0);
		 
		 save();
	}
	
	public void save() {
		
		try {
			preferenceStore.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public int tableFraction() {
		return preferenceStore.getInt(tableFraction);
	}
	
	public void tableFraction(int aFraction) {
		preferenceStore.setValue(tableFraction, aFraction);
	}
	
	public Set<String> hiddenColumnIds() {
		String names = preferenceStore.getString(tableHiddenCols);
		return SWTUtil.asStringSet(names, stringSeparator);
	}
	
	public void hiddenColumnIds(Set<String> names) {
		String nameStr = SWTUtil.asString(names, stringSeparator);
		preferenceStore.setValue(tableHiddenCols, nameStr);
	}
	
	public int selectedPropertyTab() {
		return preferenceStore.getInt(selectedPropertyTab);
	}
	
	public void selectedPropertyTab(int anIndex) {
		preferenceStore.setValue(selectedPropertyTab, anIndex);
	}
	
	public Set<String> selectedRuleNames() {
		String names = preferenceStore.getString(selectedRuleNames);
		return SWTUtil.asStringSet(names, stringSeparator);
	}
	
	public void selectedRuleNames(Collection<String> ruleNames) {
		String nameStr = SWTUtil.asString(ruleNames, stringSeparator);
		preferenceStore.setValue(selectedRuleNames, nameStr);
	}
	
	public boolean sortDirectionUp() {
		return preferenceStore.getBoolean(tableColumnSortUp);
	}
	
	public void sortDirectionUp(boolean isUp) {
		preferenceStore.setValue(tableColumnSortUp, isUp);
	}
	
	public String groupingColumnName() {
		return preferenceStore.getString(groupingColumn);
	}
	
	public void groupingColumnName(String columnName) {
		preferenceStore.setValue(groupingColumn, columnName);
	}
}
