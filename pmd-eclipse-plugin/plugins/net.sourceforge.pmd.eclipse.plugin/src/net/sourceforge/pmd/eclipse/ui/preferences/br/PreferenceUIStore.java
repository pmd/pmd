package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;

import org.eclipse.jface.preference.PreferenceStore;

/**
 * 
 * @author Brian Remedios
 */
public class PreferenceUIStore {

	private PreferenceStore preferenceStore;
		
	private static final String tableFraction 	= "ruletable.fraction";
	private static final String tableHiddenCols = "ruletable.hiddenColumns";
	private static final String tableColumnSortUp = "ruletable.sortUp";
	private static final String groupingColumn = "ruletable.groupingColumn";
	
	private static final int tableFractionDefault = 55;
	private static final char stringSeparator = ',';
	
	private static final RuleColumnDescriptor[] defaultHiddenColumns = new RuleColumnDescriptor[] {
			TextColumnDescriptor.externalURL,		TextColumnDescriptor.minLangVers,
			TextColumnDescriptor.exampleCount,		TextColumnDescriptor.since
			};

	private static final boolean defaultSortUp = false;
	
	// TODO - where to get the proper path? seem to park the file on my Ubuntu desktop for some reason 
	private static final String filename = "pmd.ui.preferences";
	
	public static final PreferenceUIStore instance = new PreferenceUIStore();
	
	private static String defaultHiddenColumnNames() {
		 Set<String> colNames = new HashSet<String>(defaultHiddenColumns.length);
		 for (int i=0; i<defaultHiddenColumns.length; i++) {
			 colNames.add(defaultHiddenColumns[i].label());
		 }
		 return SWTUtil.asString(colNames, stringSeparator);
	}
	
	private PreferenceUIStore() { 
		initialize();
	}
		
	private void initialize() {
		preferenceStore = new PreferenceStore(filename);

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
	
	public Set<String> hiddenColumnNames() {
		String names = preferenceStore.getString(tableHiddenCols);
		return SWTUtil.asStringSet(names, stringSeparator);
	}
	
	public void hiddenColumnNames(Set<String> names) {
		String nameStr = SWTUtil.asString(names, stringSeparator);
		preferenceStore.setValue(tableHiddenCols, nameStr);
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
