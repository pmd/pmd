package net.sourceforge.pmd.eclipse.ui.filters;

import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessorAdapter;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author Brian Remedios
 */
public interface FilterColumnUI {

	ItemFieldAccessor<String, FilterHolder> includeAcc = new ItemFieldAccessorAdapter<String, FilterHolder>(null) {
		public Image imageFor(FilterHolder holder) { return FilterPreferencesPage.typeIconFor(holder); }	
	};
	
	ItemFieldAccessor<String, FilterHolder> pmdAcc = new ItemFieldAccessorAdapter<String, FilterHolder>(Util.compStr) {
		public String valueFor(FilterHolder holder) { return holder.forPMD ? "Y" : ""; }
	};
	
	ItemFieldAccessor<String, FilterHolder> cpdAcc = new ItemFieldAccessorAdapter<String, FilterHolder>(Util.compStr) {
		public String valueFor(FilterHolder holder) { return holder.forCPD ? "Y" : ""; }
	};
	
	ItemFieldAccessor<String, FilterHolder> patternAcc = new ItemFieldAccessorAdapter<String, FilterHolder>(Util.compStr) {
		public String valueFor(FilterHolder holder) { return holder.pattern; }
	};

	ItemColumnDescriptor<String, FilterHolder> include 	= new ItemColumnDescriptor<String, FilterHolder>("", "   Type", SWT.LEFT, 85, false, includeAcc);
	ItemColumnDescriptor<String, FilterHolder> pmd		= new ItemColumnDescriptor<String, FilterHolder>("", "PMD", 	SWT.CENTER, 55, false, pmdAcc);
	ItemColumnDescriptor<String, FilterHolder> cpd 		= new ItemColumnDescriptor<String, FilterHolder>("", "CPD", 	SWT.CENTER, 55, false, cpdAcc);
	ItemColumnDescriptor<String, FilterHolder> pattern 	= new ItemColumnDescriptor<String, FilterHolder>("", "Pattern", SWT.LEFT, 55, true, patternAcc);

	@SuppressWarnings("rawtypes")
	ItemColumnDescriptor[] VisibleColumns = new ItemColumnDescriptor[] { include, pmd, cpd, pattern };
}
